## Git Integration Tool for ALM Octane

### Introduction

This tool can create a new branch from an Octane work item (epic, feature, user story, quality story, defect). 
Moreover, pull request and branch information can be fetched, from a git repository, based on work items' commits.

At the moment the only supported git repository is Bitbucket Server.

### Limitations

*	This solution can be used for one instance of Octane with multiple shared spaces and workspaces. In case you desire 
to use this utility on more than one Octane instance, you will need to reinstall the utility for each instance.

*	In the current version we support only one Bitbucket instance connected to an Octane instance. Multiple 
repositories and projects from that instance of Bitbucket can be connected to Octane. 
 
*	In order to fetch the pull requests or branch information correctly, the commits data must be present in Octane, 
not only in the repository.

*	If the configuration file (\conf\configuration.properties) is modified, the server must be restarted.


### Prerequisites
One instance of each application below is required for the integration:

1.	Octane – Version 12.60.47 and higher
1.	Bitbucket – Version 5.16 and higher

One of the following web servers:
1.	Tomcat – Version 7.0 and higher
1.	Jetty – Version 9.4.20 and higher


### Deployment

To generate the war please execute the ```mvn package``` goal on the root module. The war will be generated in the 
```\git-integration-for-octane\builds``` folder. In case you want to rename the war before deploying it to your web 
server you are free to do so. 

After generating the war you can read the Deployment section from the user guide, which you find in the 
[documentation](docs/Git_Integration_For_Octane_Installation_Guide-v1.1.2.pdf).

### Documentation

In case of any troubles, please read the user guide, found in the 
[documentation](/docs/Git_Integration_For_Octane_User_Guide-v1.1.2.pdf).

### What's New
* v1.1.2
    * The configuration file is loaded at startup. Some of the flaws can be detected at startup resulting in an error which can be found in Tomcat/Jetty logs file.
    * Bug fixes:
        * Application was failing at startup in case the path to the .war file contained spaces.
        * If two instances of the application were running in parallel in the same environment the log files wold be merged.
        * If multiple entities were selected at the first use for the `Get Pull Requests/Get Branch Information` features, a race condition could occur. In this case some of the UDFs would be created but not necessarily updated.
* v1.1.1
    * Branches are created with a name having the following pattern: <octane_entity_id>-<octane_entity_name>
    * Created the "logs.location" property to the configuration file. This allows the user to configure the location for the log files.
* v1.1
    * Branches with the name of the work item can be created.
    * Information about branches can be imported based on the commits of the work item. The information consist of:
        * branch name
        * link to the source code of the branch
        * name of the repository containing the branch
