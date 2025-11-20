### 粒子群算法 (particle swarm optimization)

一个完整解 $x_i=\\{x_{i1},x_{i2},...,x_{iD}\\}$ 表示解是D维向量，即一个粒子

粒子群，群体设为N , 即 $X=\\{x_1,x_2,...,x_N\\}$

一般 记 $x$ 为位置，速度为 $v$

更新位置和速度 (与物理中速度和位置更新无本质差别)

$$
\begin{align}
& v_{i}(t+1)=v_i(t)+c_1(p_i-x_i(t))R_1+c_2(g-x_i(t))R_2\\
& x_{i+1}=x_i(t)+v_i(t+1)
\end{align}
$$

t 与 t+1表示第 t 代和第 t+1 代；
$p_i$ 表示个体最好 (同一粒子不同代的比较)；

g 表示全局最好；

$c_1,c_2$ 加速度常数,也称 认知系数"cognitive coefficient" 和社会系数 "social coefficient"，用来调节粒子在个体最佳方向和全局最佳方向所采取步子的大小；

$R_1,R_2$  是两个对角矩阵，对角线的元素是从[0,1]均匀分布取值；

粒子的运动是一种半随机的，有个体和全局解的方向、加速项的随机权重组合使用。



粒子（群）的初始化：使其尽可能均匀覆盖搜索空间，是最佳选择。

**Latin hypercube  sampling**  拉丁超立方抽样
注意：位置 $x$是解，仅让 $x$ 尽可能均匀覆盖搜索空间；

$$
x_{ij}(0)\thicksim U(x_{j,min},x_{j,max})
$$

速度的更新仍有争议，如果速度将粒子轨迹扩展到越来越宽，甚至跨出搜索边界，并最终接近无穷大，这种情况被称为 ”速度爆炸“。

避免”速度爆炸“的方法
（1）速度钳制 velocity clamping

$$
\begin{align}
if \quad v_{ij}(t+1)>v_j^{max} \quad v_{ij}(t+1)=v_j^{max}\\
if \quad v_{ij}(t+1)<-v_j^{max} \quad v_{ij}(t+1)=-v_j^{max}
\end{align}
$$

$v_j^{max}$ 选择

$$
v_j^{max}=k\frac{(x_{j,max}-x_{j,min})}{2}
$$

（2） 惯性权重 inertia weight $w(t+1)$

$$
v_{i}(t+1)=w(t+1)v_i(t)+c_1(p_i-x_i(t))R_1+c_2(g-x_i(t))R_2
$$

调整惯性项 $v_i(t)$

惯性权重调整策略

<img src="ImageGallery/粒子群算法惯性权重动态调整策略.png" style="zoom:60%;" >



**线性递减权重**已被证实在许多实际引用中提供了很好的结果，建议取值 $w_{max}=0.9,w_{min}=0.4$

$c_1,c_2$ 建议取值 $c_1=2,c_2=2$ (参考:Particle swarm optimization, Proceedings of the IEEE International Conference on Neural Networks)

参考文献: Particle swarm optimization (PSO). A tutorial
