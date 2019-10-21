package cluster

import (
	. "go-scikit/model"
	"testing"
)

func TestKmeans(t *testing.T) {
	dataset := []Vector{
		{0, 0}, {1, 0}, {0, 1}, {1, 1},
		{2, 1}, {1, 2}, {2, 2}, {3, 2},
		{6, 6}, {7, 6}, {8, 6}, {6, 7},
		{7, 7}, {8, 7}, {9, 7}, {7, 8},
		{8, 8}, {9, 8}, {8, 9}, {9, 9},
	}
	centroids, clusters := Kmeans(dataset, 2)
	expectedCentroids := []Vector{
		{1.25, 1.125},
		{23.0 / 3.0, 22.0 / 3.0},
	}
	expectedClusters := []int{
		0, 0, 0, 0, 0, 0, 0, 0,
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	}
	for i := range centroids {
		if !VecEqual(centroids[i], expectedCentroids[i]) {
			t.Errorf("聚类中心结果错误\n Expected %v, res is %v", expectedCentroids[i], centroids[i])
		}
	}
	for i, v := range clusters {
		if v != expectedClusters[i] {
			t.Errorf("聚类结果错误\n Expected %v, res is %v", expectedClusters[i], v)
		}
	}
}
