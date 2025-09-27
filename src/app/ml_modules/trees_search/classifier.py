import torch
from efficientnet_pytorch import EfficientNet
from torchvision import transforms


class SpeciesClassifier:
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    transform = transforms.Compose([
        transforms.Resize((224, 224)),
        transforms.ToTensor(),
        transforms.Normalize([0.485, 0.456, 0.406],
                             [0.229, 0.224, 0.225])
    ])

    def __init__(self, tree_model_path, tree_classes, shrub_model_path, shrub_classes):
        self.tree_model = EfficientNet.from_pretrained('efficientnet-b0')
        self.tree_model._fc = torch.nn.Linear(self.tree_model._fc.in_features, len(tree_classes))
        self.tree_model.load_state_dict(torch.load(tree_model_path, map_location=self.device))
        self.tree_model = self.tree_model.to(self.device).eval()
        self.tree_classes = tree_classes

        self.shrub_model = EfficientNet.from_pretrained('efficientnet-b0')
        self.shrub_model._fc = torch.nn.Linear(self.shrub_model._fc.in_features, len(shrub_classes))
        self.shrub_model.load_state_dict(torch.load(shrub_model_path, map_location=self.device))
        self.shrub_model = self.shrub_model.to(self.device).eval()
        self.shrub_classes = shrub_classes

    def classify(self, crop, obj_class, season_label):
        if season_label != "вегетационный":
            return {"species": None, "skip_reason": "невегетационный период"}

        tensor = self.transform(crop).unsqueeze(0).to(self.device)
        with torch.no_grad():
            if obj_class == "дерево":
                out = self.tree_model(tensor)
                _, pred = torch.max(out, 1)
                return {"species": self.tree_classes[pred.item()]}
            elif obj_class == "кустарник":
                out = self.shrub_model(tensor)
                _, pred = torch.max(out, 1)
                return {"species": self.shrub_classes[pred.item()]}
            else:
                return {"species": None, "skip_reason": "неизвестный класс"}
