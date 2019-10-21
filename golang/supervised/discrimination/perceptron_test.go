package discrimination

import (
	. "go-scikit/model"
	"testing"
)

func TestPerceptron(t *testing.T) {
	d := []Vector{
		{0, 0, 0}, {1, 0, 0},
		{1, 0, 1}, {1, 1, 0},
		{0, 0, 1}, {0, 1, 1},
		{0, 1, 0}, {1, 1, 1},
	}
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
	if !VecEqual(p.WeightVector, expected) {
		t.Errorf("权向量结果错误\n Expected %v, res is %v", expected, p.WeightVector)
	}

}
