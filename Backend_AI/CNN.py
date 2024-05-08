import torch
import torch.nn as nn
import torch.optim as optim
import torch.nn.functional as F
from torchvision import transforms, datasets
from torch.utils.data import DataLoader
from load_data_c import *

def load_dataset(train=True, batch_size=32, data_root='../data', num_classes=21):
    attribute = 'category'
    phase = 'test'
    transform = transforms.Compose([
        transforms.Resize((224, 224)),
        transforms.ToTensor(),
    ])
    dataset = load_data(attribute, phase, num_classes=num_classes, transform=transform)
    return DataLoader(dataset, batch_size=batch_size, shuffle=train)

class CNNModel(nn.Module):
    def __init__(self, num_classes):
        super(CNNModel, self).__init__()
        self.conv1 = nn.Conv2d(3, 32, kernel_size=3)
        self.pool = nn.MaxPool2d(2, 2)
        self.conv2 = nn.Conv2d(32, 64, kernel_size=3)
        self.conv3 = nn.Conv2d(64, 128, kernel_size=3)
        self.conv4 = nn.Conv2d(128, 128, kernel_size=3)
        self.flatten = nn.Flatten()
        self.fc1 = nn.Linear(18432, 512)
        self.fc2 = nn.Linear(512, num_classes)

    def forward(self, x):
        x = self.pool(F.relu(self.conv1(x)))
        x = self.pool(F.relu(self.conv2(x)))
        x = self.pool(F.relu(self.conv3(x)))
        x = self.pool(F.relu(self.conv4(x)))
        x = self.flatten(x)
        x = F.relu(self.fc1(x))
        x = self.fc2(x)
        return x


def train_model(model, train_loader, val_loader, epochs, learning_rate):
    criterion = nn.CrossEntropyLoss()
    optimizer = optim.Adam(model.parameters(), lr=learning_rate)

    for epoch in range(epochs):
        model.train()
        running_loss = 0.0
        correct = 0
        total = 0
        for i, ((inputs, _), labels) in enumerate(train_loader):
            optimizer.zero_grad()
            outputs = model(inputs)
            loss = criterion(outputs, labels)
            loss.backward()
            optimizer.step()
            running_loss += loss.item()

            _, predicted = torch.max(outputs.data, 1)
            total += labels.size(0)
            correct += (predicted == labels).sum().item()

            if (i + 1) % 10 == 0:
                print(f'Epoch [{epoch + 1}/{epochs}], Step [{i + 1}/{len(train_loader)}], Loss: {running_loss / 10:.4f}, Accuracy: {100 * correct / total:.2f}%')
                running_loss = 0.0

    print('Training complete')

if __name__ == '__main__':
    num_classes = 21
    batch_size = 32
    epochs = 10
    learning_rate = 0.001
    data_dir = '../data'

    train_loader = load_dataset(train=True, batch_size=batch_size, data_root=data_dir)
    val_loader = load_dataset(train=False, batch_size=batch_size, data_root=data_dir)

    model = CNNModel(num_classes)

    train_model(model, train_loader, val_loader, epochs, learning_rate)

    torch.save(model.state_dict(), 'fashion_classifier_model.pth')
