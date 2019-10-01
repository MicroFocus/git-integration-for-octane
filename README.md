## Git Integration Tool for ALM Octane

### Introduction

This tool can be used in order to fetch pull requests from git repositories into Octane.

At the moment we support only Bitbucket.

### Limitations

•	This solution can be used for one instance of Octane with multiple shared spaces and workspaces. In case you desire to use this utility on more than one Octane instance, you will need to reinstall the utility for each instance.

•	In the current version we support only one Bitbucket instance connected to an Octane instance. Multiple repositories and projects from that instance of Bitbucket can be connected to Octane. 
 
•	In order to fetch the pull requests correctly, the commits data must be present in Octane, not only in the repository.

•	If the configuration file (\conf\configuration.properties) is modified, the server must be restarted.


### Prerequisites
One instance of each application below is required for the integration:

1.	Octane – Version 12.60.47 and higher
1.	Bitbucket – Version 5.16 and higher

One of the following web servers:
1.	Tomcat – Version 7.0 and higher
1.	Jetty – Version 9.4.20 and higher


### Deployment

To generate the war please execute the ```mvn package``` goal on the root module. The war will be generated in the ```\git-integration-for-octane\builds``` folder. In case you want to rename the war before deploying it to your web server you are free to do so. 

After generating the war you can read the Deployment section from the user guide, which you find in the [documentation](docs/Git_Integration_For_Octane_User_Guide-1.0.pdf).

### Documentation

In case of any troubles, please read the user guide, found in the [documentation](/docs/Git_Integration_For_Octane_User_Guide-1.0.pdf).
