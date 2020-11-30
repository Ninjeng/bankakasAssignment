# Bankakas Assignment

I complete the assignment for the mobile development job. 
As the requirement describes it dynamically creates the UI using the API data. 
So I created a function named **CreateLayoutFromJSON** which calls the API using Volley, then the Data is structured into arrays and objects accordingly from the API. Then for loop checks whether the UI is drop-down or text field and creates Edit text or Spinner accordingly. 
When the function creates the EditText it also checks if the Input type is string or int?  After it creates the EditText, they are inserted into an ArrayList so that EditText can be selected while validation, and also some values like hint text and regex are also stored in ArrayList. 
Similarly, the Spinner data is also generated using a for loop where the month’s data are stored in an array which is used to display the data into the Spinner.
I also complete the assignment validation for task 1, task 2, task 3. The validation of email was not working because the regex provided from the API was missing a bracket so I have done 3 solution ways to do email and phone validation.

```
 //Solution 1
// Getting the values of regex from the api and Using Pattern.match() on it.

Pattern emailPattern = Pattern.compile(regex.get(0));
Pattern phonePattern = Pattern.compile(regex.get(1));
if(!emailPattern.matcher(customer_email_string).matches())
{
  customer_email.requestFocus();
  customer_email.setError("eg("+hints.get(0)+")");
}
else if(!phonePattern.matcher(customer_phone_string).matches())
{
  customer_phone.requestFocus();
  customer_phone.setError("eg("+hints.get(1)+")");
}
```

```
//Solution 2
//Coping the regex direct from the API and managing the missing brackets.

Pattern patternEmail = Pattern.compile("/^(([^<>()[\\\\.,;:s@\\\"]]+(.[^<>()[\\\\.,;:s@\\\"]]+)*)\n" +
                        "|(\\\".+\\\"))@(([[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}])|\n" +
                        "(([a-zA-Z-0-9]+.)+[a-zA-Z]{2,}))$");
Pattern patternPhone = Pattern.compile("^[6-9]d{9}$");

if(!customer_email_string.matches(String.valueOf(patternEmail)))
{
  customer_email.requestFocus();
  customer_email.setError("eg("+hints.get(0)+")");
}
else if(!customer_phone_string.matches(String.valueOf(patternPhone)))
{
  customer_phone.requestFocus();
  customer_phone.setError("eg("+hints.get(1)+")");
}
```

```
      //Solution 3
//Using build in email and phone regex.

if(!Patterns.EMAIL_ADDRESS.matcher(customer_email_string).matches()) {
  customer_email.requestFocus();
  customer_email.setError("eg("+hints.get(0)+")");
}
else if (!Patterns.PHONE.matcher(customer_phone_string).matches()) {
  customer_phone.requestFocus();
  customer_phone.setError("eg("+hints.get(1)+")");
}

```
The most fruitful and effective way was solution 3 which is used in the application. 
