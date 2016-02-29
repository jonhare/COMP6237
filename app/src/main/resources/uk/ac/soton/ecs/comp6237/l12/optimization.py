import time
import random
import math

def wineprice(rating,age):
  peak_age=rating-50
  
  # Calculate price based on rating
  price=rating/2
  if age>peak_age:
    # Past its peak, goes bad in 10 years
    price=price*(5-(age-peak_age)/2)
  else:
    # Increases to 5x original value as it
    # approaches its peak
    price=price*(5*((age+1)/peak_age))
  if price<0: price=0
  return price

def euclidean(v1,v2):
  d=0.0
  for i in range(len(v1)):
    d+=(v1[i]-v2[i])**2
  return math.sqrt(d)


def getdistances(data,vec1):
  distancelist=[]
  
  # Loop over every item in the dataset
  for i in range(len(data)):
    vec2=data[i]['input']
    
    # Add the distance and the index
    distancelist.append((euclidean(vec1,vec2),i))
  
  # Sort by distance
  distancelist.sort()
  return distancelist

def knnestimate(data,vec1,k=5):
  # Get sorted distances
  dlist=getdistances(data,vec1)
  avg=0.0
  
  # Take the average of the top k results
  for i in range(k):
    idx=dlist[i][1]
    avg+=data[idx]['result']
  avg=avg/k
  return avg

def inverseweight(dist,num=1.0,const=0.1):
  return num/(dist+const)

def subtractweight(dist,const=1.0):
  if dist>const: 
    return 0
  else: 
    return const-dist

def gaussian(dist,sigma=5.0):
  return math.e**(-dist**2/(2*sigma**2))

def weightedknn(data,vec1,k=5,weightf=gaussian):
  # Get distances
  dlist=getdistances(data,vec1)
  avg=0.0
  totalweight=0.0
  
  # Get weighted average
  for i in range(k):
    dist=dlist[i][0]
    idx=dlist[i][1]
    weight=weightf(dist)
    avg+=weight*data[idx]['result']
    totalweight+=weight
  if totalweight==0: return 0
  avg=avg/totalweight
  return avg

def dividedata(data,test=0.05):
  trainset=[]
  testset=[]
  for row in data:
    if random.random()<test:
      testset.append(row)
    else:
      trainset.append(row)
  return trainset,testset

def testalgorithm(algf,trainset,testset):
  error=0.0
  for row in testset:
    guess=algf(trainset,row['input'])
    error+=(row['result']-guess)**2
    #print row['result'],guess
  #print error/len(testset)
  return error/len(testset)

def crossvalidate(algf,data,trials=100,test=0.1):
  error=0.0
  for i in range(trials):
    trainset,testset=dividedata(data,test)
    error+=testalgorithm(algf,trainset,testset)
  return error/trials

def wineset2():
  rows=[]
  for i in range(100):
    rating=random.random()*50+50
    age=random.random()*50
    aisle=float(random.randint(1,20))
    bottlesize=[375.0,750.0,1500.0][random.randint(0,2)]
    price=wineprice(rating,age)
    price*=(bottlesize/750)
    price*=(random.random()*0.2+0.9)
    rows.append({'input':(rating,age,aisle,bottlesize),
                 'result':price})
  return rows

def rescale(data,scale):
  scaleddata=[]
  for row in data:
    scaled=[scale[i]*row['input'][i] for i in range(len(scale))]
    scaleddata.append({'input':scaled,'result':row['result']})
  return scaleddata

def createcostfunction(algf,data):
  def costf(scale):
    sdata=rescale(data,scale)
    return crossvalidate(algf,sdata,trials=10)
  return costf

weightdomain=[(0,5)]*4

data = wineset2()
costf=createcostfunction(knnestimate,data)


def randomoptimize(domain,costf):
  best=999999999
  bestr=None
  for i in range(0,1000):
    # Create a random solution
    r=[float(random.randint(domain[i][0],domain[i][1])) 
       for i in range(len(domain))]
    
    # Get the cost
    cost=costf(r)
    
    # Compare it to the best one so far
    if cost<best:
      best=cost
      bestr=r 
  return r


def annealingoptimize(domain,costf,T=10000.0,cool=0.95,step=1):
  # Initialize the values randomly
  vec=[float(random.randint(domain[i][0],domain[i][1])) 
       for i in range(len(domain))]
  
  while T>0.1:
    # Choose one of the indices
    i=random.randint(0,len(domain)-1)

    # Choose a direction to change it
    dir=random.randint(-step,step)

    # Create a new list with one of the values changed
    vecb=vec[:]
    vecb[i]+=dir
    if vecb[i]<domain[i][0]: vecb[i]=domain[i][0]
    elif vecb[i]>domain[i][1]: vecb[i]=domain[i][1]

    # Calculate the current cost and the new cost
    ea=costf(vec)
    eb=costf(vecb)
    p=pow(math.e,(-eb-ea)/T)

    print vec,ea


    # Is it better, or does it make the probability
    # cutoff?
    if (eb<ea or random.random()<p):
      vec=vecb      

    # Decrease the temperature
    T=T*cool
  return vec

def swarmoptimize(domain,costf,popsize=20,lrate=0.1,maxv=2.0,iters=50):
  # Initialize individuals
  # current solutions
  x=[]

  # best solutions
  p=[]

  # velocities
  v=[]
  
  for i in range(0,popsize):
    vec=[float(random.randint(domain[i][0],domain[i][1])) 
         for i in range(len(domain))]
    x.append(vec)
    p.append(vec[:])
    v.append([0.0 for i in vec])
  
  
  for ml in range(0,iters):
    for i in range(0,popsize):
      # Best solution for this particle
      if costf(x[i])<costf(p[i]):
        p[i]=x[i][:]
      g=i

      # Best solution for any particle
      for j in range(0,popsize):
        if costf(p[j])<costf(p[g]): g=j
      for d in range(len(x[i])):
        # Update the velocity of this particle
        v[i][d]+=lrate*(p[i][d]-x[i][d])+lrate*(p[g][d]-x[i][d])

        # constrain velocity to a maximum
        if v[i][d]>maxv: v[i][d]=maxv
        elif v[i][d]<-maxv: v[i][d]=-maxv

        # constrain bounds of solutions
        x[i][d]+=v[i][d]
        if x[i][d]<domain[d][0]: x[i][d]=domain[d][0]
        elif x[i][d]>domain[d][1]: x[i][d]=domain[d][1]

    print p[g],costf(p[g])
  return p[g]
