# User Metadata (Java)
Sample code and demo for user metadata using Temporal. Allows for greater insights into observability by decorating Activities, Timers, and Workflows with custom text and markdown.

This sample is used as the demo for the [User Metadata slide deck](https://docs.google.com/presentation/d/12sa-eubKOxPme9--SytvJB29orAoFoRJ_a7acLEHuSg/edit?usp=sharing).

## Use Case
This project uses a generic "tools" package that lets you ship any number of utility functions as a single Temporal activity. Instead of writing one activity per helper, we register all of them under one activity entry point and route calls dynamically at runtime.
## Running the Sample

### Local Development
1. `temporal server start-dev`
2. `./gradlew runWorker`
3. `./gradlew run`

### Temporal Cloud
1. `TEMPORAL_ADDRESS={MY_NAMESPACE}.{MY_ACCOUNT}.tmprl.cloud:7233 TEMPORAL_NAMESPACE={MY_NAMESPACE}.{MY_ACCOUNT} TEMPORAL_TLS_CERT={PATH_TO_CERT}.pem TEMPORAL_TLS_KEY_PKCS8={PATH_TO_KEY}.key ./gradlew runWorker`
2. `TEMPORAL_ADDRESS={MY_NAMESPACE}.{MY_ACCOUNT}.tmprl.cloud:7233 TEMPORAL_NAMESPACE={MY_NAMESPACE}.{MY_ACCOUNT} EMPORAL_TLS_CERT={PATH_TO_CERT}.pem TEMPORAL_TLS_KEY_PKCS8={PATH_TO_KEY}.key ./gradlew run`

## Demo User Metadata
After starting the sample, use the Swing UI to start workflows with various settings.

### Static Summary

Using the Swing UI: 
* Type in a static summary and type in your name into the appropriate fields. 
* Press the 'Start workflow' button.
* The Temporal Web UI will open in your default web browser automatically.

In the Temporal Web UI:
* Click on the Summary and Details panel. You will be able to see the static summary you entered.
* Click on the 'Run' Activity in the Event History Timeline: you can see the name you input.

### Static Details

Using the Swing UI:
* Check the 'Set Static Details?' box
* The Temporal Web UI will open in your default web browser automatically.

In the Temporal Web UI:
* Click on the 'Summary and Details' panel. You will be able to see the static details section rendered with the contents of workflow-details.md.
  * This markdown is s in code: `src/main/java/com/example/swingui/SwingUI.java:134-136`

### Current Details

Using the Swing UI:
* Check the 'Set Current Details?' box
* The Temporal Web UI will open in your default web browser automatically.

In the Temporal Web UI:
* Click on the 'Current Details' panel. You will be able to see that section rendered with markdown
  * This is handled in code: `src/main/java/com/example/workflows/SimpleWorkflowImpl.java:52`

### Timers

Using the Swing UI:
* Check the 'Decorate Timers?' box
* The Temporal Web UI will open in your default web browser automatically.

In the Temporal Web UI:
* View the Event History Timeline you can see the Timers now have individual names and can render emoji
  * This is handled in code: 
    * `src/main/java/com/example/workflows/SimpleWorkflowImpl.java:46`
    * `src/main/java/com/example/workflows/SimpleWorkflowImpl.java:64`

### Activities

Using the Swing UI:
* Check the 'Decorate Activities?' box
* Select the 'sayGoodbye' radio button
* The Temporal Web UI will open in your default web browser automatically.

In the Temporal Web UI:
* View the Event History Timeline you can see the 'Run' Activity now has a label declaring which method has been called
    * This is handled in code:`src/main/java/com/example/workflows/SimpleWorkflowImpl.java:37`

