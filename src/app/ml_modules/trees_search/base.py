from pathlib import Path
import io

import torch
from ultralytics import YOLO
from PIL import Image


class TreesSearcher:
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

    def run(self, image) -> list:
        model_path = self._model_path()
        model = self.load_model(model_path)
        results, preds = self.predict(image, model, conf_thres=0.25)
        return preds

        # image = self.get_images_from_results(results)
        # return image

    @classmethod
    def load_model(cls, model_path: str):
        model = YOLO(model_path)
        model.to(cls.device)
        return model

    @staticmethod
    def predict(image_data, model, conf_thres=0.30):
        image = Image.open(io.BytesIO(image_data)).convert("RGB")
        w, h = image.size
        results = model(image, conf=conf_thres)

        predictions = []
        for result in results:
            for box in result.boxes:
                cls_id = int(box.cls[0])
                conf = float(box.conf[0])
                label = model.names[cls_id]
                x1, y1, x2, y2 = box.xyxy[0].tolist()

                x1 /= w
                x2 /= w
                y1 /= h
                y2 /= h

                predictions.append({
                    "label": label,
                    "confidence": conf,
                    "bbox_norm": [x1, y1, x2, y2],
                    "bbox_abs": [box.xyxy[0].tolist()]
                })
        return results, predictions

    @staticmethod
    def get_images_from_results(results):
        image_bytes_list = []

        for r in results:
            im_array = r.plot()
            im = Image.fromarray(im_array[..., ::-1])

            img_byte_arr = io.BytesIO()
            im.save(img_byte_arr, format='PNG')
            img_byte_arr.seek(0)

            image_bytes_list.append(img_byte_arr.getvalue())

        return image_bytes_list

    @staticmethod
    def _model_path() -> Path:
        return Path(__file__).resolve().parent / "trees.pt"
