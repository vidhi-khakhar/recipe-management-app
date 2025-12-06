# Minutes Week 4

| Key           | Value         |
| ------------- | ------------- |
| Date:         | 05-12-2025    |
| Time:         | 14:45         |
| Location:     | DW PC Hall 3  |
| Chair:        | Vidhi Khakhar |
| Minute Taker: | Ioana Caprita |
| Attendees:   | Marijn Rutten, Lorenzo Morixe, Wouter Stoop, Simon de Graauw |
---
Agenda Items:
1. **Opening by chair** (1 min)  
2. **Check-in:** How is everyone doing? (2 min)  
	- Everybody seems to be doing exceedingly fine, except for the ever-present gloom of the approaching midterms. Over the past week, there has been a leap of progress regarding the project, since a lot of the basic functionality was implemented over this period.  
3. **Announcements by the team** (2 min)  
	- On the weekend following the midterms, Marijn and Ioana announced that they will be unavailable and so the division of tasks will take this into account.
4. **Approval of the agenda** — Does anyone have any additions? (1 min)  
5. **Approval of last minutes** — Did everyone read the last minutes? Do you approve of these?  (1min)
6. **Announcements by the Team and/or the TA** (3 min)  
	- Next week, from Wednesday (December 10 till 17,) till the Wednesday in Week 6, Gitlab will be unavailable, so everybody should pull before that time. Contributions are not expected in Week 5 (the midterm week), but they are still expected in Week 6 (despite Gitlab maintenance).
	- Buddycheck should be completed this Friday and should follow the AID model for giving Tips and Tops. Note that this time, the feedback pass/fail is formative but at the end of the course, if somebody fails this, they fail the course. 
	- This week we should also get formative grading of Tasks & Planning, so on Gitlab issues should be descriptive, labels should reflect current status and time estimates should be reasonably accurate.
7. **Presentation of the current app** (5 min)  -15min mark
	- Simon presented the add recipe functionality. A window pops up and user types in the title of the recipe. The changes are then reflected to the main UI and to the database, so that they persist even after closing the app.
	- **NB:** The Database should be centralized. Database runs on the server. 
8. **Talking Points:**
   - **Status of the buddy check and weekly criteria** (2 min)
	   - Everybody completed Buddycheck asides from Marijn. 
	   - Everybody met weekly criteria asides from Wouter, who got a message stating that the MR was already merged in main, for some unknown reason. 
   - **Current status of everyone's tasks, what is done, what is left** (3 min)
	   - Simon: implemented adding recipes, though the ingredinets not visible yet depending on which recipe is selected
	   - Marijn: IngredientService done, not yet tests for IngredientRepository  
	   - Lorenzo: IngredientServerUtils done, yet to merge IngredientController and yet to do tests
	   - Wouter: RecipeServerUtils and tests for it done
	   - Vidhi: did the nutritional values UI, yet to do clone recipe 
	   - Ioana: did the add ingredient in recipe UI, almost done with downloable version of recipe, yet to connet the ingredient added to the recipe 
   - **Inventory of tasks for the project** - What issues will be doing next milestone (10 min)
    - We want to drastically reduce the number of dependencies for the next milestone.
    - We want to make sure everyone's work load is balanced.
    - Remaining tasks:
		- cloning the recipe 
		- refresh button
		- add/edit/delete instructions 
		- connect recipe to ingredient part 
		- edit recipe title 
		- test IngredientRepository 
		- connect ingredients to recipes in database (inside RecipeServerUitls)
		- see global ingredients when adding ingredients 
		- localhost string not hardcoded  
		- CRUD ingredient functionality to UI
**NB:** Basic functionaltiy should be done before the Christmas break.
   - **Division of tasks for next week** (5 min)
   - We want to drastically reduce the number of dependencies for the next milestone.
   - We want to make sure everyone's work load is balanced.
	   - We decided that the division of tasks will be done outside the meeting so that we can assess the dependencies between us properly. Vidhi will create the issues and then we assign ourselves to the issue we want. This way we will be more aware of the extent of the functionality and there will be less overlapping. 
9. **Summarize action points:** Who, what, when? (3 min) 
10. **Feedback round:** What went well and what can be improved next time? (2 min)
	- Vidhi is an awesome chair 
	- Time was estimated well
	- She is not biased!
11. **Planned meeting duration vs actual duration:** Where/why did you misestimate? (1 min)  
12. **Question round:** Does anyone have anything to add before the meeting closes? (2 min)
	- Vidhi said to add the issues for the milestone tomorrow so that the burndown chart is a nicer linear represantation of reality
13. **Closure** (1 min)
