# Job Posting and Application App

## Overview
This is a job posting and application platform designed to bridge the gap between employers and employees. The app allows employers to post job listings and manage applicants, while enabling employees to search for jobs, apply, and track their application status.

## Features

### Employer Features
- **Post Jobs**: Employers can create job postings with details like title, description, location, job type, and salary.
- **View Applicants**: Employers can view a list of applicants for their job postings and review their profiles.
- **Shortlist or Reject Applicants**: Employers can mark applications as "Shortlisted" or "Rejected."
- **Send Job Offers**: Employers can send formal job offers, including salary and start date, to shortlisted candidates.
- **Track Hiring Status**: Employers can track the hiring status of each application.
- **Secure Payments**: Employers can pay employees securely through paypal

### Employee Features
- **Search and Apply for Jobs**: Employees can browse job listings, search based on criteria, and apply directly.
- **Application Status Tracking**: Employees can view the status of their applications, such as "Submitted," "Shortlisted," or "Rejected."
- **Preferred Jobs**: Employees can mark specific job titles as preferred to receive notifications about new postings.
- **Push Notifications**: Employees receive real-time updates for new job postings and job application status changes.
- **Google Maps**: Employees can browse and search jobs by location, with a map view.

### Integration Features
- **Firebase Integration**: Used for authentication, database storage, and push notifications.
- **Push Notifications**: Real-time notifications for new job postings and application updates using Firebase Cloud Messaging.
- **Google Maps API**: Enabled location-based job searches.
- **PayPal API**: Integrated for secure payment processing.

## Agile Methodologies
- **Sprint Planning**: User stories were divided and assigned in sprint meetings.
- **Pair Programming**: Developers collaborated to write and debug code together.
- **TDD**: Test-driven development ensured robust and reliable code.
- **Code Reviews**: Regular code reviews maintained quality and clean code practices.

## Technologies Used
- **Front-End**: XML for layouts, Java for Android development.
- **Back-End**: Firebase Realtime Database for data storage.
- **APIs**:
  - Firebase Authentication
  - Firebase Cloud Messaging for push notifications
  - Google Maps API for location-based features
  - PayPal API for payment processing

## System Architecture
- **Database**: Firebase Realtime Database structured into nodes for users, job postings, applications, and preferred jobs.
- **Notifications**: Firebase Cloud Messaging topics are used to send updates based on user preferences.
- **Modular Code**: Features are split into distinct modules for better maintainability.

## Challenges
- **Debugging a Large Codebase**: Managing and debugging interconnected features.
- **Integrating APIs**: Ensuring seamless integration of Firebase, Google Maps, and PayPal APIs.
- **Real-Time Notifications**: Implementing push notifications for both preferred jobs and application status updates.

## Future Improvements
- Adding instant chat between employers and employees.
- Enhancing search filters for job listings.
- Expanding support for more payment gateways.

## Setup and Installation
1. Clone the repository.
   ```bash
   git clone https://github.com/toriibarnard/QuickCash.git
   ```
2. Open the project in Android Studio.
3. Configure Firebase:
   - Add your `google-services.json` file to the `app/` directory.
   - Enable Firebase Authentication, Realtime Database, and Cloud Messaging.
4. Set up Google Maps API:
   - Obtain an API key and add it to your `AndroidManifest.xml`.
5. Build and run the project on an emulator or physical device.

## Usage
1. **Employer Login**:
   - Create job postings.
   - View and manage applications.
2. **Employee Login**:
   - Search and apply for jobs.
   - Track application status.
3. **Notifications**:
   - Receive updates about preferred jobs and application statuses.

## Contributors
- Team of 7 developers following Agile methodologies.