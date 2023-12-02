# TODO: Generative AI version
• Use a generative AI, like chatGPT, to create a compiling, running solution for this assignment. The AI solution does not have to be correct, it just has to compile and run. You may work with the AI to produce a complete, correct solution or you may use the AI to create a skeleton, minimal solution. Keep a record of every prompt you give to the AI to produce the solution.
• Put the AI solution that you end up with in the docs folder of your repository.
• Create a markdown file that contains all of the prompts you used to create that solution and put it in the docs folder
• Write a code review of the solution created by the AI. Use the prompts in Homework2 to help you organize the code review. Include a section about functionality and correctness.
• The code review should also be a markdown file and must be in the docs directory.
• You may be asked to discuss the AI solution as part of the grading interview.

# TODO: FAQ
Should the AI program in the docs folder have a build.gradle file and be able to compile inside the GP2 folder?
Answer: There is no requirement, but your work will be easier if you put the AI-generated program in a subfolder of the docs folder and use the gradle standard layout along with a build.gradle file. You can eliminate checkstyle for the AI version if you want or use it as a source of things to talk about for your code review

# TODO: FAQ
For the docs folder, can we structure it however we like since it's not autograded?
Answer: Yes. You might find it useful to put the AI generated code in its own subfolder. The docs folder must also have a markdown file that contains the prompts you used to get to the AI generated code as well as your code review of the AI generated code in a second markdown file.

# TODO: FAQ
Does the AI version need to have all the same methods described in the assignment description?
Answer: Ideally yes, but we're not autograding the AI generated assignment so it won't matter if it isn't perfect. Get it to the point that it is running (possibly not completely correctly).

# TODO: FAQ
What's supposed to be in the aiSolution folder? Is it supposed to contain all the classes?
Answer: aiSolution is the name I would give to a subfolder of the docs folder. In it, I would put a build.gradle file and the src\java\main set of directories that held the AI solution. That way I could use gradle to build that solution.