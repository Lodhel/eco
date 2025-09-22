from pathlib import Path

import torch
import torch.nn as nn
from torchvision import transforms
from efficientnet_pytorch import EfficientNet
from PIL import Image

from src.app.ml_modules.plant_module.labels_names import class_names, class_labels_ru


class PlantML:

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    transform = transforms.Compose([
        transforms.Resize((224, 224)),
        transforms.ToTensor(),
        transforms.Normalize([0.485, 0.456, 0.406],
                             [0.229, 0.224, 0.225])
    ])

    def run(self, image_path: str):
        model_path = self._model_path()
        model = self.load_model(str(model_path))
        result = self.predict(image_path, model)

        return result

    @classmethod
    def load_model(cls, model_path: str):
        model_instance = EfficientNet.from_pretrained('efficientnet-b0')
        num_features = model_instance._fc.in_features
        model_instance._fc = nn.Linear(num_features, len(class_names))
        model_instance.load_state_dict(torch.load(model_path, map_location=cls.device))
        model_instance = model_instance.to(cls.device)
        model_instance.eval()
        return model_instance

    @classmethod
    def predict(cls, image_path: str, model):
        image = Image.open(image_path).convert("RGB")
        image = cls.transform(image).unsqueeze(0).to(cls.device)
        with torch.no_grad():
            output = model(image)
            _, pred = torch.max(output, 1)
        eng_label = class_names[pred.item()]
        ru_label = class_labels_ru.get(eng_label, eng_label)
        return ru_label

    @staticmethod
    def _model_path() -> Path:
        return Path(__file__).resolve().parent / "ml_38.pth"
