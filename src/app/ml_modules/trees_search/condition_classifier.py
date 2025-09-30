import torch
from torchvision import transforms
import torch.nn as nn
from torchvision import models

from src.app.ml_modules.trees_search.condition_classes.tree_classes import TREE_CONDITION_CLASSES


class TreeConditionClassifier:
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    transform = transforms.Compose([
        transforms.Resize((224, 224)),
        transforms.ToTensor(),
        transforms.Normalize([0.485, 0.456, 0.406],
                             [0.229, 0.224, 0.225])
    ])
    condition_classes = TREE_CONDITION_CLASSES

    def __init__(self, model_path, threshold=0.5):
        self.threshold = threshold

        self.model = models.efficientnet_b0(pretrained=True)
        in_features = self.model.classifier[1].in_features
        self.model.classifier[1] = nn.Linear(in_features, len(self.condition_classes))

        self.model.load_state_dict(torch.load(model_path, map_location=self.device))
        self.model = self.model.to(self.device).eval()

    def classify(self, crop):
        tensor = self.transform(crop).unsqueeze(0).to(self.device)
        with torch.no_grad():
            out = self.model(tensor)
            probs = torch.sigmoid(out).cpu().numpy()[0]

        results = {}
        for cls, p in zip(self.condition_classes, probs):
            results[cls] = 1 if p >= self.threshold else 0
        return results
