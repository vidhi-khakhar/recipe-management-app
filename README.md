# CSEP Project  
## Server
This repository contains the template for the CSE project. Please extend this README.md with sufficient instructions that will illustrate for your TA and the course staff how they can run your project.  
  
To run the template project from the command line, you either need to have [Maven](https://maven.apache.org/install.html) installed on your local system (`mvn`) or you need to use the Maven wrapper (`mvnw`). You can then execute  
```
mvn -pl server -am spring-boot:run  
```
to run the server and  
```
mvn -pl client -am javafx:run  
```
to run the client. **Please note that the server needs to be running, before you can start the client.**  
## Client 
Assuming that you have [Maven](https://maven.apache.org/install.html) installed, you can run the project out-of-the-box from your terminal via  
```
mvn -pl client -am javafx:run  
```
from your project root (not from within the `client` folder!).  
Starting the client within your IDE (Eclipse/IntelliJ) requires setting up OpenJFX.  
First download (and unzip!) an [OpenJFX SDK](https://openjfx.io).  
Make sure that the download *matches your Java JDK version*.  
Then create a *run configuration* for the `Main` class and add the following *VM* commands (which, in IntelliJ, are hidden by default):  
  ```
--module-path="/path/to/javafx-sdk/lib"    
--add-modules=javafx.controls,javafx.fxml,javafx.wew
  ```
Adjust the module path to *your* local download location and make sure you adapt the path  
to the `lib`(!) directory (not just the directory that you unzipped)...  
*Tip:* Windows paths are different, they uses `\` as path separator and starts with a drive letter like `C:`.  
*Tip:* Make sure not to forget the `/lib` at the end of the path.  
*Tip:* Double-check that the path is correct. If you receive abstract errors, like `Module javafx.web not found`  
or a segmentation fault, you are likely not pointing to the right folder. Try opening the folder and check that  
it contains several .jar files, such as `javafx.controls.jar`.
### GROUP 78 
The syntax for the config path parameters is of the following format:
```
--cfg "path/to/config.json"
```
The path can be modified via Edit configurations of `Main.java` and then in Program Arguments you pass in the above (*Note:* it may be / or \ depending on the OS)
## Meaningful Additions to Implemented Extensions 
### Nutritional Values
#### What did we add?
- There is a pie-chart available of the ingredient nutritional values, so that the distribution of fat, carbs and protein can be easily visualized (click `Health Overview`)
- There is also an indication of how healthy an ingredient is.
- There is also an indication of how healthy a recipe is, calculating according to the following formula: 
	- *(sum(kcals/100g_ingredient * amount)) / total_weight_of_recipe*
- The healthiness is also indicated via a visual label, according to the following:
	- healthy - green
	- moderately healthy - yellow
	- unhealthy - red
- There is also an information button that informs of the way the healthiness is decided (bottom left corner, under Nutritional Values section)
- If a recipe is changed, the refresh button must be clicked so as to see the changes reflected.
#### Why is it meaningful?
This is meaningful as it helps people visualize the proportions of the nutritional values of an ingredient, thereby making the feature appropriate for most age groups (even kids!) and those who find it difficult to associate mere numerical symbols to the material reality of the percentages.
### Searching for Recipe
#### What did we add?
- There is a sort functionality that sorts the list:
	- alphabetically:
		- from A → Z  
		- from Z →A 
	- creation date:
		- NEW → OLD
		- OLD →NEW
- This allows for a more efficient search for the user.
- Searching "t1 t2 t3" is considered as "t1" AND "t2" AND "t3", so it is treated as an AND operations, so the query must satisfy all the search terms
#### Why is it meaningful?
This is meaningful as it can aid users in keeping the sidebar of the recipes neatly arranged in their preferred order, so that they can quickly find their recipe at a glance instead of having to query a search for it.
### Shopping List
#### What did we add?
- In the shopping list window, there is a `remind me!` button, where users can set a date and time when they want to their shopping
- As long as the UI is open, the user will get notified at that time of the contents of their shopping list. 
#### Why is it meaningful?
It can help users who lead hectic lifestyles, with a busy schedule. They might forget to do their groceries, but the app will notify them if they set a reminder, so that they do not need to have one additional thing on their mind. 
### Live Language Switch
#### What did we add?
- Asides from English, Dutch & Spanish, the app can also be translated to Romanian. 
#### Why is it meaningful?
It is meaningful as it makes the app more inclusive. Since the percentage of Romanians is high (especially in the CS department at TU Delft), it is particularly helpful to have the app provide support for this language.
## Troubleshooting 
Try these if the application stops working as intended / crashes:
- Delete the `config.json` file in the project root.
- Delete the H2 database files (`.db`).
*Nota bene:* Since the uses custom fonts, some systems will display cryptic-looking characters if that font is not installed on their machine. If such is the case, replace all instances of the custom font with `arial`. 
Upon a clean git clonig, the build might fail unless `mvn install` is used.


