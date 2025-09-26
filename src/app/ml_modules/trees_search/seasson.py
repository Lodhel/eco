import torch
from PIL import Image

from efficientnet_pytorch import EfficientNet
from torchvision import transforms


class SeasonClassifier:

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    transform = transforms.Compose([
        transforms.Resize((224, 224)),
        transforms.ToTensor(),
        transforms.Normalize([0.485, 0.456, 0.406],
                             [0.229, 0.224, 0.225])
    ])

    def __init__(self, model_path, class_names):
        self.model = EfficientNet.from_pretrained('efficientnet-b0')
        num_features = self.model._fc.in_features
        self.model._fc = torch.nn.Linear(num_features, len(class_names))
        self.model.load_state_dict(torch.load(model_path, map_location=self.device))
        self.model = self.model.to(self.device).eval()
        self.class_names = class_names

    def predict(self, image):
        tensor = self.transform(image).unsqueeze(0).to(self.device)
        with torch.no_grad():
            output = self.model(tensor)
            _, pred = torch.max(output, 1)
        return self.class_names[pred.item()], image
