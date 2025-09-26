import os

from ultralytics import YOLO


class ObjectDetector:
    def __init__(self, model_path):
        self.model = YOLO(model_path)

    def detect(self, image, season_label, save_crops=False, crop_dir="crops"):
        results = self.model.predict(image)
        outputs = []
        for r in results:
            for box in r.boxes:
                cls_id = int(box.cls[0])
                cls_name = self.model.names[cls_id]
                x1, y1, x2, y2 = map(int, box.xyxy[0])
                crop = image.crop((x1, y1, x2, y2))
                if save_crops:
                    os.makedirs(crop_dir, exist_ok=True)
                    crop_path = f"{crop_dir}/{cls_name}_{x1}_{y1}.jpg"
                    crop.save(crop_path)
                outputs.append({
                    "crop": crop,
                    "class": cls_name,
                    "season": season_label,
                    "bbox": (x1, y1, x2, y2)
                })
        return outputs, self.model
