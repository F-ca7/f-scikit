package model

import (
	"math"
)

type Vector []float64

// 向量相等
func VecEqual(u, v Vector) bool {
	for i := range u {
		if math.Abs(u[i]-v[i]) > 1e-3 {
			return false
		}
	}
	return true
}

// 向量点乘
func VecDot(u, v Vector) (res float64) {
	if len(u) != len(v) {
		panic("向量长度不同")
	}
	res = 0
	for i, val := range u {
		res += val * v[i]
	}
	return
}

// 向量放大/缩小
func VecMultByPureNum(u Vector, size float64) (res Vector) {
	res = make(Vector, len(u))
	for i, val := range u {
		res[i] = val * size
	}
	return
}

// 向量加法
func VecAdd(u, v Vector) (res Vector) {
	res = make(Vector, len(u))
	for i, val := range u {
		res[i] = val + v[i]
	}
	return
}
