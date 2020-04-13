# Ohio Daily Virus Update

A simple project with the purpose of providing daily updates on the number of confirmed cases of COVID-19 in Ohio through email. Data is gathered
from the Ohio government's website through direct parsing from a link to a CSV file that contains the data for all cases in Ohio. Data is then 
formatted and sent through an email using the Java Mail API.

### Prerequisites

This project is being developed using jdk-11.0.6, the download can be found [here](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html). 
Two APIs are used in this project including the JavaMail API version 1.6.2., found [here](https://github.com/javaee/javamail/releases) 
and the JavaBeans Activation Framework version 1.1.1., found [here](https://www.oracle.com/technetwork/java/javase/downloads/index-135046.html). 

Note, for the gmail account used to send the emails, "Less secure app access" will likely need to be turned on for this program to work. This can be 
done in the relevant google account [security settings](https://myaccount.google.com/security).

## Getting Started

Once the project has been imported into eclipse, the path to the two external libraries must be reconfigured. This is done in eclipse through the 
Libraries tab in the Java Build Path for the project. Further details on this can be found 
[here](https://help.eclipse.org/2019-12/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Freference%2Fref-properties-build-path.htm). 

After being properly configured, Eclipse can be used to export the project to a runnable jar file. For instruction on this, see 
[here](https://help.eclipse.org/2019-12/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Ftasks%2Ftasks-37.htm). The program may be run through 
the command line.

```
$ java -jar "name_of_runnable_jar_file.jar"
```

## Authors

* **Joseph Saurine** - *Initial work* - (https://github.com/saurinej)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Thank you Stephen Mills and Ryan Farrar for your help and suggestions.