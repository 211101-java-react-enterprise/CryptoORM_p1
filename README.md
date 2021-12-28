# CryptoORM_p1
Built by Andrew Aslakson and Cole Paris.
ORM project created in revature batch 211101 java-react-enterprise curriculum.

This project was built for use in a servlet based webApplication that allowed users to pretend to trade CryptoCurrencies.
Github Link: https://github.com/211101-java-react-enterprise/CryptoWallet_p1
------------------------------------------------

### Mapper
Our mapper is of a singleton design and requires that you not only get an instance, but before it is used it's 
required that you pass your database credentials in useing the mapper's setProperties method. This method takes
in a properties object that should look like the following:
+++++++++++++++++++++++++++++++++++++++++++++++++
url={URL to database here}
username={username}
password={password}
++++++++++++++++++++++++++++++++++++++++++++++++++
--------------------------------------------------

### Method Naming Conventions
Our naming conventions for using the methods of our mapper follow the naming conventions in SQL
select - returns a result set with the result from the select query
insert - Inserts a row into database
update - updates a row in the database
delete - delets a row or in database
--------------------------------------------------

### Annotations
Our ORM uses three Annotations in order to properly model our entity classes:
@Table - Class-Level annotation, sits above a class and provides database table name

@Column - Field-level annotation, sits above the fields that define columns in the table.
          This annotation also allows you to provide the correct name for a column in the database.
          
@Value - Method-level annotation, sits above getter methods for fields marked with @Column.
         Note: This annotation must be given the corresponding column attribute in order to properly 
               Locate Which column is associated with the getter.
