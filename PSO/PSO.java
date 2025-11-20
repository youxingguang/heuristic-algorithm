package ParticleSwarmOptimization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/*
 * 粒子群优化方法
 * min x^2+y^2
 * s.t. (x-4)^2+(y-2)^2<=2^2
 */
public class PSO {
    	
	static int N=20;//粒子群的大小规模[20,1000]
	static int D=2;//粒子的维度 本例只涉及两个变量,就是二维
	static int K=60;//迭代次数[50,100]
	static double w=0.9;//惯性权重[0.4,2] 0.9,1.2,1.5,1.8
	static double c1=2;//学习因子1 表示粒子下一步动作源于自身经验部分所占的权重,将粒子推向个体最优
	static double c2=1.5;//学习因子2 表示粒子下一步动作源于其他粒子经验部分所占的权重,将粒子推向群体最优
	//典型取值 c1=c2=2   c1=1.6 c2=1.8   c1=1.6 c2=2
	
	
	//v_i=v_i+c1*rand()*(pbest_i-x_i)+c2*rand()*(gbest-x_i)
	double[][] speed;//速度 下一步移动方向=惯性方向+个体最优方向+群体最优方向,辅助
	
	//x_i=x_i+v_i
	double[][] position;//位置 上一步位置+下一步速度,实际的解
	
	double cx=4,cy=2,R=2;//约束圆的参数
	
	
	
	double[][] pbest;//记录每个粒子的最佳个体(现在与过去自己比)
	double[] gbest;//全局最好的粒子
	
	/*初始位置:使用拉丁方抽样,速度不用拉丁抽样方法
	 * 如果位置不可行,再投影到可行域内
	 * 
	 * 抽样方法 随机1...N个排列 
	 */
	public void init(int N,int D)
	{
		// x\in {2,6}  y\in {0,4}
	    double[][] bound= {{cx-R,cx+R},{cy-R,cy+R}};
	
		Random rand=new Random();
		
		speed=new double[N][D];
		position=new double[N][D];
		
		//初始位置
		for(int i=0;i<N;i++)
		{
			int[] perm=randomPermutation(N);
			for(int d=0;d<D;d++)
			{
				double u=rand.nextDouble();
				double sample=(perm[i]-1+u)/N;
				//lb+sample*(ub-lb)
				position[i][d]=bound[d][0]+sample*(bound[d][1]-bound[d][0]);
				
				//速度    (-1 or 1)*(变量上界-下界）*10%
				double v=u>0.5?-1:1;
				speed[i][d]=v*(bound[d][1]-bound[d][0])*0.1;
			}
		}
	
	}
	//评价函数
	public double evaluate(double[] solution)
	{
		double obj=solution[0]*solution[0]+solution[1]*solution[1];
		return obj;
	}
	
	public void solve()
	{
		pbest=new double[N][D];
		gbest=new double[D];
		
		int k=1;
		for(int i=0;i<N;i++)
		{
			
			if(check(position[i]))
			{
				pbest[i]=Arrays.copyOf(position[i],D);
			}else
			{
				//修正后 复制
				 position[i]=repair(position[i]);
				 pbest[i]=Arrays.copyOf(position[i],D);
			}
			//评估最好的粒子
			if(i==0)
			{
				gbest=Arrays.copyOf(pbest[i], D);
			}else
			{
				if(evaluate(pbest[i])<evaluate(gbest))
				{
					gbest=Arrays.copyOf(pbest[i], D);
				}
			}
			
		}
		
		
		k++;
		while(k<K)
		{
			
			//更新下一代 位置和速度
			Random rand=new Random();
			for(int i=0;i<N;i++)
			{
				for(int d=0;d<D;d++)
				{
					double r=rand.nextDouble()*2-1;
					speed[i][d]=w*speed[i][d]+c1*r*(pbest[i][d]-position[i][d])+c2*r*(gbest[d]-position[i][d]);
					position[i][d]+=speed[i][d];
				}
			}
			
			
			//评估筛选
			for(int i=0;i<N;i++)
			{
				
				if(check(position[i]))
				{
					if(evaluate(position[i])<evaluate(pbest[i]))
					{
						pbest[i]=Arrays.copyOf(position[i],D);
					}
				}else
				{
					//修正后 复制
					 position[i]=repair(position[i]);
					 if(evaluate(position[i])<evaluate(pbest[i]))
					 {
							pbest[i]=Arrays.copyOf(position[i],D);
					 }
				}
				
				if(evaluate(pbest[i])<evaluate(gbest))
				{
					gbest=Arrays.copyOf(pbest[i], D);
				}
			}
			
			k++;
		}
	}
	
	
	
	
	
	//对不可行解 修复
	public double[] repair(double[] solution)
	{
	
			//如果不可行,投影到边界
			double dx=solution[0]-cx;
			double dy=solution[1]-cy;
			double dist=Math.sqrt(dx*dx+dy*dy);
			double scale=R/dist;
			double px=cx+dx*scale;
			double py=cy+dy*scale;
			return new double[] {px,py};
		
	}
	//生成1..N的随机排列 
	public int[] randomPermutation(int N)
	{
		List<Integer> list=new ArrayList<>();
		for(int i=1;i<=N;i++)
		{
			list.add(i);
		}
		Collections.shuffle(list);
		int[] arr=new int[N];
		for(int i=0;i<N;i++)
		{
			arr[i]=list.get(i);
		}
		return arr;		
	}
	//检查粒子 是否可行 (满足 (x-4)^2+(y-2)^2<=2^2 )
	public boolean check(double[] solution)
	{
	
		double lhs=(solution[0]-cx)*(solution[0]-cx)+(solution[1]-cy)*(solution[1]-cy);
		if(lhs<=R*R)
		{
			return true;
		}else
			return false;
	}
	
	public static void main(String[] args) {
			PSO pso=new PSO();
			pso.init(N, D);
			pso.solve();
			System.out.println("最好目标值："+pso.evaluate(pso.gbest));
			for(int d=0;d<D;d++)
			{
			   System.out.println(pso.gbest[d]);
			}
		
		
		
		
	}

}
