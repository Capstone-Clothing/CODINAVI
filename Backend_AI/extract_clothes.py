from flask import Flask, request, jsonify
import cv2
import numpy as np
from sklearn.cluster import KMeans

app = Flask(__name__)

def process_image(image):
    image_rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    
    image_reshaped = image_rgb.reshape((image_rgb.shape[0] * image_rgb.shape[1], 3))
    
    kmeans = KMeans(n_clusters=5)
    kmeans.fit(image_reshaped)
    centroids = kmeans.cluster_centers_
    labels = kmeans.labels_
    
    unique_labels, counts = np.unique(labels, return_counts=True)
    color_percentages = counts / counts.sum()
    
    major_colors = []
    for centroid in centroids:
        major_colors.append(centroid.astype(int).tolist())

    return major_colors, color_percentages

@app.route('/process_image', methods=['POST'])
def process_image_route():
    if 'image' not in request.files:
        return jsonify({'error': 'No image provided'}), 400
    image_file = request.files['image']
    
    nparr = np.fromstring(image_file.read(), np.uint8)
    image_cv2 = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

    major_colors, color_percentages = process_image(image_cv2)

    return jsonify({'major_colors': major_colors, 'color_percentages': color_percentages}), 200

if __name__ == '__main__':
    app.run(debug=True)
