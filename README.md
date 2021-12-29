# CryptoORM_p1
Built by Andrew Aslakson and Cole Paris.

ORM project created in revature batch 211101 java-react-enterprise curriculum.

This project was built for use in a servlet based web application that allowed users to simulate cryptocurrency trading.

Github Link: https://github.com/211101-java-react-enterprise/CryptoWallet_p1

------------------------------------------------

### Mapper
Our mapper is of a singleton design (only has one instance) and must be set before used. This is done by passing in database credentials to the mapper's setProperty method which takes in a properties object that should look like the following:
<pre>
url={URL to database here}
username={username}
password={password}
</pre>
--------------------------------------------------

### Method Naming Conventions
Our naming conventions for using the methods of our mapper follow the naming conventions in SQL
<pre>
select - returns a result set with the result from the select query
insert - Inserts a row into database
update - updates a row in the database
delete - deletes a row in the database
</pre>
--------------------------------------------------

### Annotations
Our ORM uses three Annotations in order to properly model our entity classes:
<pre>
@Table - Class-Level annotation, sits above a class and provides database table name

@Column - Field-level annotation, sits above the fields that define columns in the table.
          This annotation also allows you to provide the correct name for a column in the database.
          
@Value - Method-level annotation, sits above getter methods for fields marked with @Column.
         Note: This annotation must be given the corresponding column attribute in order to properly 
               Locate Which column is associated with the getter.
</pre>
