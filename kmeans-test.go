package main

import (
	"fmt"
	"go-scikit/unsupervised/cluster"
)

func main() {
	dataset := [][]float64{
		{0, 0}, {1, 0}, {0, 1}, {1, 1},
		{2, 1}, {1, 2}, {2, 2}, {3, 2},
		{6, 6}, {7, 6}, {8, 6}, {6, 7},
		{7, 7}, {8, 7}, {9, 7}, {7, 8},
		{8, 8}, {9, 8}, {8, 9}, {9, 9}}
	centroids, clusters := cluster.Kmeans(dataset, 2)
	fmt.Printf("centroids:%v\ncluster:%v\n", centroids, clusters)
	for i := range centroids {
		fmt.Printf("第%d类的样本:", i)
		for j := range clusters {
			if clusters[j] == i {
				fmt.Printf("%v ", dataset[j])
			}
		}
		fmt.Println()
	}
}
