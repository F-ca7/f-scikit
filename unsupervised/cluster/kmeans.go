package cluster

import (
	"errors"
	. "go-scikit/model"
	"math"
)

// 获取两个样本点的平方欧氏距离
func squaredEuclideanDist(s1, s2 Vector) float64 {
	var squaredDist float64
	for i := range s1 {
		squaredDist += (s1[i] - s2[i]) * (s1[i] - s2[i])
	}
	return squaredDist
}

// 获取一组样本点的中心
func getCentroid(samples []Vector) Vector {
	// 样本数
	sampleNums := len(samples)
	// 特征数
	featureNums := len(samples[0])

	centroid := make(Vector, featureNums)
	for i := 0; i < featureNums; i++ {
		var sum float64
		for j := 0; j < sampleNums; j++ {
			sum += samples[j][i]
		}
		centroid[i] = sum / float64(sampleNums)
	}
	return centroid
}

// kmeans均值聚类
// K为设定的聚类中心数
func Kmeans(dataset []Vector, K int) ([]Vector, []int) {
	// 样本数
	sampleNums := len(dataset)
	// 特征数
	featureNums := len(dataset[0])
	if sampleNums < K {
		panic(errors.New("样本数少于聚类中心数"))
	}
	centroids := make([]Vector, K)
	clusters := make([]int, sampleNums)
	// 选择前K个样本作为初始聚类中心
	for i := 0; i < K; i++ {
		centroids[i] = make(Vector, featureNums)
		copy(centroids[i], dataset[i])
	}
	// 新聚类中心是否改变
	changed := true
	var dist float64
	for changed {
		changed = false
		// 计算样本到K个中心点的距离
		for x := 0; x < sampleNums; x++ {
			distMin := math.Inf(1)
			minIdx := -1
			for i := 0; i < K; i++ {
				dist = squaredEuclideanDist(centroids[i], dataset[x])
				if dist < distMin {
					// 按最短距离原则将其余样本分配到聚类中心的某一个
					distMin = dist
					minIdx = i
				}
			}
			// 保存聚类中心点
			clusters[x] = minIdx
		}
		// 计算新的聚类中心
		for i := 0; i < K; i++ {
			samples := make([]Vector, 0)
			for j := 0; j < sampleNums; j++ {
				if clusters[j] == i {
					// 找到所有属于第i类的样本
					samples = append(samples, dataset[j])
				}
			}
			// 聚类中心是否发生改变
			centroid := getCentroid(samples)
			// fmt.Printf("原中心 %v; 新中心 %v\n", centroids[i], centroid)
			if !changed && !VecEqual(centroids[i], centroid) {
				changed = true
			}
			centroids[i] = centroid
		}

	}

	return centroids, clusters
}
