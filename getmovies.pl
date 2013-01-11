#!/usr/bin/perl -w

use LWP::UserAgent;

my $ua = new LWP::UserAgent;
my $i = $ARGV[0]; 
$oldindex = 21420;
my $url='http://www.imdb.com/search/title?colors=color&countries=us&languages=en%7C1&production_status=released&release_date=2000-01-01,2013&sort=moviemeter,asc&start=' . $i . '&title_type=feature&user_rating=5.0,10&view=simple';
$ua->timeout(120);
my $request = new HTTP::Request('GET', $url);
    my $response = $ua->request($request);
    my $content = $response->content();
    
    for ($i = 0; $i <99; $i++) 
    {    
        $index = index($content, "/title/tt", $oldindex);
        #print $oldindex;
        $oldindex = $index+1;
        $endindex = index($content, "</a>", $index);
        print substr $content, $index+19, $endindex-($index+19);
        print "\n";
    }
