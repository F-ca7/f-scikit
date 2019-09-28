package discrimination

const (
	MAX_TURN = 10000 // 最大迭代轮数
)

type Vector []float64

// 两类线性可分感知器
type TwoClassPerceptron struct {
	Dataset      []Vector // 数据集
	Class        []int    // 类别，以+/-1区分
	WeightVector Vector   // 权向量
	IncreFactor  float64  // 校正增量
}

// 向量点乘
func vecDot(u, v Vector) (res float64) {
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
func vecMultByPureNum(u Vector, size float64) (res Vector) {
	res = make(Vector, len(u))
	for i, val := range u {
		res[i] = val * size
	}
	return
}

// 向量加法
func vecAdd(u, v Vector) (res Vector) {
	res = make(Vector, len(u))
	for i, val := range u {
		res[i] = val + v[i]
	}
	return
}

// 开始训练
func (p *TwoClassPerceptron) Train() {
	// 样本数
	sampleNums := len(p.Dataset)
	// 特征数
	featureNums := len(p.Dataset[0])
	if len(p.WeightVector) != featureNums+1 {
		panic("权向量初始长度错误")
	}
	w := p.WeightVector
	// 规范化增广矩阵
	augMat := make([]Vector, sampleNums)
	for i := 0; i < sampleNums; i++ {
		augMat[i] = make(Vector, featureNums+1)
		class := p.Class[i]
		for j := 0; j < featureNums; j++ {
			augMat[i][j] = float64(class) * p.Dataset[i][j]
		}
		augMat[i][featureNums] = float64(class)
	}
	// 一轮迭代的错误次数
	errCnt := 1
	// 总轮数
	turnCnt := 0
	// 直到错误次数为0才停止迭代
	for errCnt >= 1 && turnCnt <= MAX_TURN {
		turnCnt++
		// fmt.Printf("第%d轮迭代\n", turnCnt)
		errCnt = 0
		for i := 0; i < sampleNums; i++ {
			res := vecDot(w, augMat[i])
			if res <= 0 {
				// 分类错误
				w = vecAdd(w, vecMultByPureNum(augMat[i], p.IncreFactor))
				errCnt++
			}
		}
	}
	p.WeightVector = w
}
