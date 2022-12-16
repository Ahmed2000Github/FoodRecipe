




 CREATE (ee:User {name: 'Ahmed', email: 'ahmedelrhaouti2000@gmail.com', password: "password",id:'63ec5ddf-6497-4380-be45-979c1ewb4bea-user'}) 
 CREATE (ee:User {name: 'Ismail', email: 'ismail@gmail.com', password: "password",id:'63ec5ddf-6497-4380-be45-979c1esb4bea-user'})

 MATCH (ee:User {email: 'ahmedelrhaouti2000@gmail.com'}) REMOVE ee:User RETURN ee.name, labels(ee)

 MATCH (user:User) WHERE user.email = 'Ahmed' RETURN user;

 CREATE (ee:Recipe {title: 'title', type: 'food', url: "url", date:'12/12/2022 7:00 PM', like:12,id:'63ec5ddf-6497-4380-be45-979c1eeb4bea'}) RETURN id(ee)

 MATCH (ee {title: 'title'}) REMOVE ee:Recipe RETURN ee.title, labels(ee)

 MATCH (ee:Recipe) WHERE ee.title = 'title' RETURN ee;

 MATCH (recipe) WHERE id(recipe) = 11 RETURN recipe;

CREATE (user:User {id:''}),(recipe:Recipe {id:'63ec5ddf-6497-4380-be45-979c1eeb4bea'}) , (user)-[:SHARE ]->(recipe)

MATCH (a:User),(b:Recipe) WHERE a.id = "63ec5ddf-6497-4380-be45-979c1ewb4bea" AND b.id = "63ec5ddf-6497-4380-be45-979c1eeb4bea" CREATE (a)-[r:SHARE]->(b) RETURN r

MATCH (n)  WHERE id(n) = 11 DELETE n


CREATE (ee:Ingridient {group:'others',name: 'orange', quantity: '2',id:'73ec5ddf-6497-4380-be45-979c1eeb4bea'}) RETURN id(ee)
CREATE (ee:Nutrition {key:'value',id:'73ec2ddf-6497-4380-be45-979c1eeb4bea'}) RETURN id(ee)

CREATE (ee:Step {num:'1',decription: 'description', time: '2 min',id:'73eb5ddf-6497-4380-be45-979c1eeb4bea'}) RETURN id(ee)

CREATE (ee:Comment {date:'12/12/2022 7:00 PM',decription: 'description',id:'73eb5ddg-6497-4380-be45-979c1eeb4bea'}) RETURN id(ee)

MATCH (a:Recipe),(b:Ingridient) WHERE a.id = "63ec5ddf-6497-4380-be45-979c1eeb4bea" AND b.id = "73ec5ddf-6497-4380-be45-979c1eeb4bea" CREATE (a)-[r:HAS_INGRIDIENT]->(b) RETURN r

MATCH (a:Recipe),(b:Step) WHERE a.id = "63ec5ddf-6497-4380-be45-979c1eeb4bea" AND b.id = "73eb5ddf-6497-4380-be45-979c1eeb4bea" CREATE (a)-[r:HAS_STEP]->(b) RETURN r

MATCH (a:Recipe),(b:Nutrition) WHERE a.id = "63ec5ddf-6497-4380-be45-979c1eeb4bea" AND b.id = "73ec2ddf-6497-4380-be45-979c1eeb4bea" CREATE (a)-[r:HAS_NUTRITION]->(b) RETURN r

MATCH (a:Recipe),(b:Comment) WHERE a.id = "63ec5ddf-6497-4380-be45-979c1eeb4bea" AND b.id = "73eb5ddg-6497-4380-be45-979c1eeb4bea" CREATE (a)-[r:HAS_COMMENT]->(b) RETURN r

MATCH (a:User),(b:Comment) WHERE a.id = "63ec5ddf-6497-4380-be45-979c1ewb4bea" AND b.id = "73eb5ddg-6497-4380-be45-979c1eeb4bea" CREATE (a)-[r:WRITE_COMMENT]->(b) RETURN r
 

MATCH (:User)-[r:SHARE]-(:Recipe) DELETE r

MATCH (:Recipe {id: 'user6'})-[r:HAS_COMMENT]-(comment) RETURN r
MATCH (:Recipe {id: 'user6'})-[r:HAS_COMMENT]-(comment) RETURN count(r)

MATCH ()-[r]-() DELETE r

MATCH (n)  DELETE n

MATCH (recipe:Recipe {id: 'user6'})-[r:HAS_COMMENT]-(comment) RETURN comment.date,comment.decription

MATCH (user:User )-[r:WRITE_COMMENT]-(comment:Comment {id:'1c36e690-2262-42e1-8a0f-c72e2e926934-comment'}) RETURN user.name

MATCH (recipe:Recipe {id:'id'})-[r:HAS_NUTRITION]->(nut:Nutrition) RETURN nut

MATCH (user:User)-[r:SHARE]->(recipe:Recipe {id:'79b29a2e-de9c-4929-a9ab-9ecaeb8da35c-recipe'}) RETURN recipe

MATCH (n {id:'4f718274-06e7-4ed1-b6b4-42dad2d6bcf3-nutrition'} ) WITH keys(n) AS p UNWIND p AS x WITH DISTINCT x WHERE x =~ ".*" RETURN collect(x);


MATCH (user:User) WHERE user.name  =~ '.*(?i)sddd.*' OR user.email  =~ '.*(?i)ismail@gmail.com.*' RETURN user.id,user.name,user.email,user.password