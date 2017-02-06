# Wikipedia pageview data pipeline

## Description

Simple application that computes the top 25 pages on Wikipedia for each of the Wikipedia sub-domains.

### Features

* Accepts input parameters for the date and hour of data to analyze (defaults to the current date/hour if not passed)
* Downloads the page view counts from wikipedia for the given date/hour in https://wikitech.wikimedia.org/wiki/Analytics/Data/Pagecounts-all-sites format from http://dumps.wikimedia.org/other/pagecounts-all-sites/
* Eliminates any pages found in this blacklist: https://s3.amazonaws.com/dd-interview-data/data_engineer/wikipedia/blacklist_domains_and_pages
* Compute the top 25 articles for the given day and hour by total pageviews for each unique domain in the remaining data.
* Save the results to a file, either locally or on S3, sorted by domain and number of pageviews for easy perusal
* Only run these steps if necessary; that is, not rerun if the work has already been done for the given day and hour
* Be capable of being run for a range of dates and hours

## Additional things needed to operate this application in a production setting

* Job scheduler
* Well defined storage location
* Compile to Jar and write a wrapper script rather than using SBT
* Publish to maven or other artifact repository

## Potential changes to run automatically for each hour of the day

* Locate on AWS EC2 host
* Change it to write to S3 using InstanceProfileCredentialsProvider

## Testing

* Testing of inputs is done by passing a known vector of strings to the function which generates the TopK results
* Schedule for date range and compare results
* Schedule for single hour and compare results

## Potential design improvements on this application design

* Distribute input ranges to workers to run tasks in parallel (Only if a lot of parallel access are supported by the data source)


## Usage 

### Run for Current Hour
```
sbt "run-main dd.Cli"
```
 
### Run for Single Hour
```
sbt "run-main dd.Cli 2016-08-02 5"
```
 
### Run for Date Range 
```
sbt "run-main topk.Cli 2016-08-01 22 2016-08-02 2"
```

### Sample Output

```
PS C:\app\datadog> sbt "run-main topk.Cli 2016-08-02 2 2016-08-02 3"
Loaded blacklist of length 57114
Hours in range: 1
Starting work for 2016-08-02 02
Reading from https://dumps.wikimedia.org/other/pagecounts-all-sites/2016/2016-08/pagecounts-20160802-020000.gz
Ignored 1000 entries due to blacklist
Processed 1000000 entries
Ignored 2000 entries due to blacklist
Ignored 3000 entries due to blacklist
Processed 2000000 entries
Ignored 4000 entries due to blacklist
Ignored 5000 entries due to blacklist
Processed 3000000 entries
Ignored 6000 entries due to blacklist
Processed 4000000 entries
Ignored 7000 entries due to blacklist
Ignored 8000 entries due to blacklist
Processed 5000000 entries
Ignored 9000 entries due to blacklist
Ignored 10000 entries due to blacklist
Processed 6000000 entries
Ignored 11000 entries due to blacklist
Processed 7000000 entries
Finished reading. Total views=30368979
Writing results to 20160802-02.txt
Finished: 2016-08-02 02
[success] Total time: 50 s
```


## Authors and Copyright

Copyright (C) 2017 Jason Mar
