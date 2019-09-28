package discrimination

import (
	"math"
	"testing"
)

// 向量相等
func vecEqual(u, v Vector) bool {
	for i := range u {
		if math.Abs(u[i]-v[i]) > 1e-3 {
			return false
		}
	}
	return true
}

func TestPerceptron(t *testing.T) {
	d := []Vector{{0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {1, 1, 0},
		{0, 0, 1}, {0, 1, 1}, {0, 1, 0}, {1, 1, 1}}
	class := []int{1, 1, 1, 1, -1, -1, -1, -1}
	wVec := Vector{-1, -2, -2, 0}

	p := &TwoClassPerceptron{
		Dataset:      d,
		Class:        class,
		WeightVector: wVec,
		IncreFactor:  1,
	}
	p.Train()
	expected := Vector{3, -2, -3, 1}
	if !vecEqual(p.WeightVector, expected) {
		t.Errorf("权向量结果错误\n Expected %v, res is %v", expected, p.WeightVector)
	}

}
