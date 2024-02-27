// NOTE: Implementation heavily based on code from
// https://medium.com/@nirmal1067/java-k-mean-clustering-with-math-4077a6e2b6c0

import java.util.*;

public class KMeansClustering {
    private final List<Double[]> features_data;
    private HashMap<String, List<Double[]>> clusters;
    private HashMap<String, Double[]> centroid_values;
    private final int k_clusters;
    private int n_epochs = 1000;

    public static void main(String[] args) {
        // Establish initial dataset
        Double[][] data = {{ .23d, .34d, .67d },
                           { .23d, .84d, .47d },
                           { .21d, .64d, .97d },
                           { .493d, .424d, .7d },
                           { .94d, .64d, .932d },
                           { .13d, .84d, .899d }};
        List<Double[]> dataSet = new ArrayList<>(Arrays.asList(data));

        // Create Clusterer object - initialises with random centroids
        KMeansClustering clusterer = new KMeansClustering(dataSet, 3);

        // Form and refine clusters given initial centroids
        HashMap<String, Double[]> clusterCentroidValues = clusterer.buildCluster();

        // Print out final clusters
        clusterCentroidValues.forEach((k, val) ->
                System.out.println("ClusterId/CategoryId: " + k + " Centroid values: " + Arrays.toString(val)));

        // Test fitting new point with final clusters
        Double[] new_datapoint = { .131d, .841d, .89d };
        String category = clusterer.findClosestCluster(new_datapoint);
        System.out.println("Assigned cluster for " + Arrays.toString(new_datapoint) + ": " + category);
    }

    public KMeansClustering(List<Double[]> featuresData, int k_Clusters) {
        this.features_data = featuresData;
        this.k_clusters = k_Clusters;
        this.initializeModel();
    }

    public HashMap<String, Double[]> buildCluster() {
        initialClustersCreation();
        return refiningKClusters();
    }

    public int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

     /**
     * This method creates k clusters and initialises their value to initial random
     * values
     */
    private void initializeModel() {
        clusters = new HashMap<>();
        centroid_values = new HashMap<>();
        for (int i = 0; i < k_clusters; i++) {
            List<Double[]> list = new ArrayList<>();
            int randomIndex = getRandomNumberUsingNextInt(0, features_data.size());
            Double[] initialValues = features_data.get(randomIndex);
            String clusterId = "" + i;
            centroid_values.put(clusterId, initialValues);
            System.out.println("Initial centroid value for Cluster: " + clusterId + " Values : "
                    + Arrays.toString(initialValues));
            clusters.put(clusterId, list);
        }
    }

    /**
      * This method classifies data points into clusters for first time.
      * After this method executes, data points will be classified/divided
      * into initial k clusters.
      **/
    public void initialClustersCreation() {
        coreClassifyingLogic(features_data);
    }

    /**
     * This is the core processing logic:
     * <ul>
     * <li>
     * We keep iterating over the data set and
     * classifying the data points based on their Euclidean distance from each cluster's centroid.
     * </li>
     * <li>
     * The centroid of each cluster also keeps getting changed based on the mean data points
     * in that cluster. (This is an important step to ensure the clusters keep improving.)
     * </li>
     * </ul>
     * The process stops when we reach number of epochs defined. The stopping logic can
     * be driven by a number of epochs, when the centroid values stop changing substantially ,
     * or the data points stop moving across clusters.
     */
    private HashMap<String, Double[]> refiningKClusters() {
        while (keepOnClassifying()) {
            for (String key : clusters.keySet()) {
                List<Double[]> oldFeatures = clusters.get(key);

                // Move cluster value based on the mean of data points in the cluster

                Double[] clusterValues = centroid_values.get(key);
                Double[] newCentroid = new Double[clusterValues.length];
                Arrays.fill(newCentroid, 0.0d);

                for (Double[] data : oldFeatures) {
                    for (int j = 0; j < newCentroid.length; j++) {
                        newCentroid[j] = newCentroid[j] + data[j];
                    }
                }

                for (int i = 0; i < newCentroid.length; i++) {
                    newCentroid[i] = newCentroid[i] / oldFeatures.size();
                }

//                System.out.println(" Old centroid value for Cluster: " + key + " Old Value : "
//                        + Arrays.toString(clusterValues) + " New Values: " + Arrays.toString(newCentroid)
//                        + " Previous Number of data points in this cluster: " + oldFeatures.size());

                // Reassign new mean values to centroids based on mean calculation done above.
                centroid_values.put(key, newCentroid);

                // This is method does reassignment of Cluster based on Euclidean distance
                coreClassifyingLogic(oldFeatures, key);
            }
        }

        return centroid_values;
    }

    /**
     * Checks whether the number of epochs has been reached
     */
    private boolean keepOnClassifying() {
        if (n_epochs <= 0) {
            return false;
        }
        n_epochs = n_epochs - 1;
        // TODO Add logic to consider other parameter like cluster centroid value not
        //  changing and cluster data point not shuffling between clusters
        return true;

    }

    private void coreClassifyingLogic(List<Double[]> tempFeaturesData) {
//        for (Double[] tempFeaturesDatum : tempFeaturesData) {
//            String destinationCluster = findClosestCluster(tempFeaturesDatum);
//
//            List<Double[]> classifiedData = clusters.get(destinationCluster);
//
//            if (classifiedData == null) {
//                classifiedData = new ArrayList<>();
//            }
//
//            classifiedData.add(tempFeaturesDatum);
//        }

        Iterator<Double[]> iterator = tempFeaturesData.iterator();

        while (iterator.hasNext()) {
            Double[] data = iterator.next();
            String destinationCluster = findClosestCluster(data);
            List<Double[]> classifiedData = clusters.get(destinationCluster);

            if (classifiedData == null) {
                classifiedData = new ArrayList<>();
                clusters.put(destinationCluster, classifiedData);
            }

            classifiedData.add(data);
            iterator.remove(); // Remove the current element from tempFeaturesData
        }
    }

    private String findClosestCluster(Double[] tempFeaturesDatum) {
        Double euclideanDistance = Double.MAX_VALUE;
        String destinationCluster = "";
        for (String clusterKey : centroid_values.keySet()) {
            Double[] clusterCentroids = centroid_values.get(clusterKey);
            double sumSquareDistance = 0.00d;
            for (int k = 0; k < tempFeaturesDatum.length; k++) {

                double tempDistance = (clusterCentroids[k] - tempFeaturesDatum[k]);
                double squareDistance = Math.pow(tempDistance, 2);
                sumSquareDistance = sumSquareDistance + squareDistance;
            }

            Double clusterSpecificDistance = Math.sqrt(sumSquareDistance);

            if (clusterSpecificDistance.compareTo(euclideanDistance) < 0) {
                euclideanDistance = clusterSpecificDistance;
                destinationCluster = clusterKey;
            }
        }
        return destinationCluster;
    }

    private void coreClassifyingLogic(List<Double[]> tempFeaturesData, String previousClusterKey) {
        List<Integer> indexToBeRemoved = new ArrayList<>();

        for (int i = 0; i < tempFeaturesData.size(); i++) {
            Double[] data = tempFeaturesData.get(i);

            String newClusterKey = findClosestCluster(data);

            // if the old cluster is different to new cluster
            if (! newClusterKey.equalsIgnoreCase(previousClusterKey)) {
                List<Double[]> classifiedData = clusters.get(newClusterKey);

                if (classifiedData == null) {
                    classifiedData = new ArrayList<>();
                }

                classifiedData.add(data);
                indexToBeRemoved.add(i);  // Cannot remove in iteration because of concurrent modification exception
            }
        }

        // Remove all replaced features
        for (Integer i : indexToBeRemoved) {
            tempFeaturesData.remove((int) i);
        }

    }

}
