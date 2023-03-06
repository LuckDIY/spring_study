# spring_study
用于学习spring相关知识

#### 组件理解:
BeanWrapper :  <br />
   spring 提供的操作javaBean属性的工具类
````
BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(new Object());
bw.setPropertyValue("设置属性key","设置属性value");
````
xxxAware : <br />
   spring 提供，如果某个类想获取xxx，则通过实习xxxAware来获取
