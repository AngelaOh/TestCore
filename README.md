# TestCore

TestCore is an Android Studio app that helps the user (public school teachers) build tests that are based on state standards. Once the user logs in, TestCore pulls the corresponding state standards from https://commonstandardsproject.com/developers and allows teachers to create a new test, edit an existing test, view standards, and create new questions via standards. 

Testing holds such importance in schools. Not only are assessments a primary method of getting feedback on teaching and tracking student progress, school-wide resources can sometimes be allocated by a school's test scores. With that, teachers should be provided with better tools to create standards-driven tests. That is what TestCore was intented to help with. 

## Getting Started

1. Create new Android Studio Project and clone from this repo. File->New->Project from Version Control->Github
1. Create account and get API Key from https://commonstandardsproject.com/developers
  - Be sure to hide your API Key in local .gradle.properties file. 
  - Standards_ApiKey="XXXXXXX"
1. Add Firebase Auth, Firestore, and Volley depencencies. 
  - For Firebase, be sure to hide your API Key in local .gradle.properties file. 
  - Google_ApiKey="XXXXXXX"

## Built With

* [Android Studio 3.4.2](https://developer.android.com/studio) 

## Authors

* **Angela Oh** - *Initial work* - [AngelaOh](https://github.com/AngelaOh)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Thank you to all the teachers that do the hard work every day. 
