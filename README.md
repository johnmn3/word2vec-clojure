# Clojure implementation of word2vec

[![Build Status](https://travis-ci.org/tamamu/word2vec-clojure.svg?branch=master)](https://travis-ci.org/tamamu/word2vec-clojure)

WIP

- [x] Download the data.
- [x] Build the dictionary and replace rare words with UNK token.
- [ ] Function to generate a training batch for the skip-gram model.
- [ ] Build and train a skip-gram model.
- [ ] Begin training.
- [ ] Visualize the embeddings.

## Log

06/29
I try to use "neanderthal", but my PC can't build atlas-lapack.
If I can disable CPU throttling, the build wlii be successful.

06/30
Use core.matrix for alternative to neanderthal.

07/01
I'm writing generate-batch function now.
Almost finished but the result is strange...x-(
Try to fix.

## Usage

WIP

## License

Copyright © 2016 Tamamu

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
