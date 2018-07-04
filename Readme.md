## Requirements to build this project

* JDK 8
* Maven 3.x

### Build project
`mvn clean install`

### Run project
`mvn exec:java`

## Assumptions
1. Looking at 20 minute time window +/- 10 minutes from sale date.
2. 3 different outputs listed in `net.rajeesh.nectar.Main` on #81, #87, #95.

## Alternate solutions
1. Combine files into one and tag transactions with sale or pour. Use iterators to traverse list.
2. Use spark.
3. Use database table, similar to #1. 
  
