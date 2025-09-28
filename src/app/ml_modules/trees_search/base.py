import os
from pathlib import Path
import io

import torch
from PIL import Image, ImageDraw, ImageFont

from src.app.ml_modules.trees_search.classifier import SpeciesClassifier
from src.app.ml_modules.trees_search.detector import ObjectDetector
from src.app.ml_modules.trees_search.seasson import SeasonClassifier


class TreesSearcher:
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    season_clf = SeasonClassifier
    detector = ObjectDetector
    species_clf = SpeciesClassifier

    def run(self, image_file):
        season_classes = ["вегетационный", "невегетационный"]

        season_clf = self.season_clf(self._model_path('period.pth'), season_classes)
        detector = self.detector(self._model_path('yolo_detector.pt'))
        species_clf = self.species_clf(
            self._model_path('classifier_trees.pth'),
            self._model_path('classifier_shrubs.pth')
        )

        season_label, image = season_clf.predict(image_file)
        detections, model = detector.detect(image.copy(), season_label)
        predictions = []
        season = 'вегетационный'
        for d in detections:
            season = d["season"]
            res = species_clf.classify(d["crop"], d["class"], d["season"])
            d.update(res)

            predictions.append({
                "name_plant": d["species"],
                "label": d["class"],
                "season": d["season"],
                "bbox_abs": d["bbox_abs"],
                "bbox_norm": d["bbox_norm"]
            })

        image = self.predict(image, detections)

        return {
            'image': image,
            'season': season,
            'preds': predictions
        }

    @staticmethod
    def predict(image, detections, font_path=None):
        draw = ImageDraw.Draw(image)
        if font_path and os.path.exists(font_path):
            font = ImageFont.truetype(font_path, 20)
        else:
            font = ImageFont.load_default()

        for d in detections:
            x1, y1, x2, y2 = d["bbox_abs"]
            species = d.get("species")

            draw.rectangle([x1, y1, x2, y2], outline="red", width=3)

            if species:
                bbox = draw.textbbox((x1, y1), species, font=font)
                text_w, text_h = bbox[2] - bbox[0], bbox[3] - bbox[1]

                text_bg = [x1, y1 - text_h - 4, x1 + text_w + 4, y1]
                draw.rectangle(text_bg, fill="red")
                draw.text((x1 + 2, y1 - text_h - 2), species, fill="white", font=font)

        return image

    @staticmethod
    def get_images_from_results(results):
        if len(results) == 0:
            return []

        r = results[0]
        im_array = r.plot()
        im = Image.fromarray(im_array[..., ::-1])

        img_byte_arr = io.BytesIO()
        im.save(img_byte_arr, format='PNG')
        img_byte_arr.seek(0)

        return img_byte_arr.getvalue()

    @staticmethod
    def _model_path(name: str) -> Path:
        return Path(__file__).resolve().parent / name
