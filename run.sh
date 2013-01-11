#!/bin/bash

for i in {1..9001..100}
    do
        perl getmovies.pl $i
    done
