from sklearn.cluster import KMeans
import numpy as np
import cv2
import color_data
from PIL import Image
from matplotlib import pyplot as plt
import white
import white2

def extraction(cloth, back):

    background_removed_img = cloth.copy()

    difference = cv2.subtract(back, cloth)  # 배경 이미지와 배경에 옷이 추가된 이미지의 다른 부분만
    background_removed_img[np.where((difference < [50, 50, 50]).all(axis=2))] = [0, 0, 0]
    return background_removed_img


def centroid_histogram(clt):

    numLabels = np.arange(0, len(np.unique(clt.labels_)) + 1)
    (hist, _) = np.histogram(clt.labels_, bins=numLabels)
    hist = hist.astype("float")
    hist /= hist.sum()
    return hist


# 추출할 색의 범위 지정 및 각 색의 RGB 값과 그 점유율을 리스트로 반환, 히스토그램으로 표현
def plot_colors(hist, centroids):

    percent_list = []
    color_list = []

    # 배경은 퍼센트 부분에 포함x
    for (percent, color) in zip(hist, centroids):
        if int(color.astype("uint8").tolist()[0]) == int(color.astype("uint8").tolist()[1]) == int(
                color.astype("uint8").tolist()[2]) == 0:
            pass
        else:
            percent_list.append(percent)
    bar = np.zeros((50, int(300 * (sum(percent_list))), 3), dtype="uint8")
    startX = 0

    for (percent, color) in zip(hist, centroids):
        endX = startX + (percent * 300)
        if int(color.astype("uint8").tolist()[0]) == int(color.astype("uint8").tolist()[1]) == int(
                color.astype("uint8").tolist()[2]) == 0:
            pass
        else:
            cv2.rectangle(bar, (int(startX), 0), (int(endX), 50), color.astype("uint8").tolist(), -1)
            startX = endX
            color_list.append(color)
    return bar, percent_list, color_list


def image_color_cluster(image, k=5):

    image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    image = image.reshape((image.shape[0] * image.shape[1], 3))
    clt = KMeans(n_clusters=k)
    clt.fit(image)
    hist = centroid_histogram(clt)
    bar, p_list, c_list = plot_colors(hist, clt.cluster_centers_)
    return c_list, p_list


def find_color_name():

    cloth = Image.open('../image_data/cloth.jpg').convert('RGB')
    cloth.save('../image_data/cloth.png', 'png')

    back = Image.open('../image_data/back.jpg').convert('RGB')
    back.save('../image_data/back.png', 'png')

    picture = cv2.imread('../image_data/cloth.png')
    back = cv2.imread('../image_data/back.png')

    equ = white2.equ_Hist(picture)
    idx = white2.extractWhite(equ)
    wh_front = white2.WB(idx, picture)
    equ = white2.equ_Hist(back)
    idx = white2.extractWhite(equ)
    wh_back = white2.WB(idx, back)
    img4 = extraction(wh_front, wh_back)
    cv2.imwrite("../image_data/after_white4.jpg", img4)

    img = extraction(picture, back)
    cv2.imwrite("../image_data/before_white.jpg", img)
    img2 = white.white_b(img)
    cv2.imwrite("../image_data/after_white.jpg", img2)
    equ = white2.equ_Hist(img)
    idx = white2.extractWhite(equ)
    img3 = white2.WB(idx, img)
    cv2.imwrite("../image_data/after_white3.jpg", img3)

    rgb_list, percent_list = image_color_cluster(img)
    color_name_list = color_data.extract_color(rgb_list)

    priority_color_list = []
    priority_color_list.append(color_name_list[percent_list.index(max(percent_list))])
    percent_list[percent_list.index(max(percent_list))] = 0
    priority_color_list.append(color_name_list[percent_list.index(max(percent_list))])

    return priority_color_list
