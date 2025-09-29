import torch
from efficientnet_pytorch import EfficientNet
from loguru import logger
from torchvision import transforms

from src.app.ml_modules.trees_search.plant_classes.tree_classes import TREE_CLASSES
from src.app.ml_modules.trees_search.plant_classes.shrub_classes import SHRUB_CLASSES


class SpeciesClassifier:

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    transform = transforms.Compose([
        transforms.Resize((224, 224)),
        transforms.ToTensor(),
        transforms.Normalize([0.485, 0.456, 0.406],
                             [0.229, 0.224, 0.225])
    ])

    tree_classes = TREE_CLASSES
    shrub_classes = SHRUB_CLASSES
    conf_threshold = 0.4

    def __init__(self, tree_model_path, shrub_model_path):

        self.tree_model = EfficientNet.from_pretrained('efficientnet-b0')
        self.tree_model._fc = torch.nn.Linear(self.tree_model._fc.in_features, len(self.tree_classes))
        self.tree_model.load_state_dict(torch.load(tree_model_path, map_location=self.device))
        self.tree_model = self.tree_model.to(self.device).eval()

        self.shrub_model = EfficientNet.from_pretrained('efficientnet-b0')
        self.shrub_model._fc = torch.nn.Linear(self.shrub_model._fc.in_features, len(self.shrub_classes))
        self.shrub_model.load_state_dict(torch.load(shrub_model_path, map_location=self.device))
        self.shrub_model = self.shrub_model.to(self.device).eval()

    def classify(self, crop, obj_class, season_label):
        if season_label != "вегетационный":
            return {"species": None, "skip_reason": "невегетационный период"}

        tensor = self.transform(crop).unsqueeze(0).to(self.device)
        with torch.no_grad():
            if obj_class == "дерево":
                out = self.tree_model(tensor)
                probs = torch.nn.functional.softmax(out, dim=1)
                conf, pred = torch.max(probs, 1)
                return {"species": self.tree_classes[pred.item()], "confidence": conf.item()}

            elif obj_class == "кустарник":
                out = self.shrub_model(tensor)
                probs = torch.nn.functional.softmax(out, dim=1)
                conf, pred = torch.max(probs, 1)
                if conf.item() < self.conf_threshold:
                    logger.info('Неизвестный вид')
                    return {"species": "Неизвестный вид", "confidence": conf.item()}
                return {"species": self.shrub_classes[pred.item()], "confidence": conf.item()}

            else:
                return {"species": None, "skip_reason": "не определено"}
