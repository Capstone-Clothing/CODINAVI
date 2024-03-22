import torch
import torch.nn as nn
import torch.optim as optim
import torch.nn.functional as F
from torchvision import transforms, datasets
from torch.utils.data import DataLoader
from dataset import load_data,KFashionDataset


# 모델 정의
class CNNModel(nn.Module):
    def __init__(self, num_classes):
        super(CNNModel, self).__init__()
        self.conv1 = nn.Conv2d(3, 32, kernel_size=3)
        self.pool = nn.MaxPool2d(2, 2)
        self.conv2 = nn.Conv2d(32, 64, kernel_size=3)
        self.conv3 = nn.Conv2d(64, 128, kernel_size=3)
        self.conv4 = nn.Conv2d(128, 128, kernel_size=3)
        self.flatten = nn.Flatten()
        self.fc1 = nn.Linear(128 * 6 * 6, 512)
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




# 모델 학습
def train_model(model, train_loader, val_loader, epochs, learning_rate):
    criterion = nn.CrossEntropyLoss()
    optimizer = optim.Adam(model.parameters(), lr=learning_rate)

    for epoch in range(epochs):
        model.train()
        for inputs, labels, indices in train_loader:
            optimizer.zero_grad()
            outputs = model(inputs)
            loss = criterion(outputs, labels)
            loss.backward()
            optimizer.step()

        # 검증 단계는 여기에 추가합니다

# 파라미터 설정
if __name__ == '__main__':
    # 파라미터 설정
    num_classes = 10  # 분류할 클래스의 수
    batch_size = 32
    epochs = 10
    learning_rate = 0.001
    data_dir = './data'  # 데이터셋이 있는 폴더 경로

    # 데이터 로드
    train_loader = load_data(train=True, batch_size=batch_size, data_root=data_dir)
    val_loader = load_data(train=False, batch_size=batch_size, data_root=data_dir)

    # 모델 생성
    model = CNNModel(num_classes)

    # 모델 학습
    train_model(model, train_loader, val_loader, epochs, learning_rate)

    # 모델 저장
    torch.save(model.state_dict(), 'fashion_classifier_model.pth')