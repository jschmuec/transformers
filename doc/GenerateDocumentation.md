Generating Documentation
===

A transformation between an input format and an output format
is a function of an input, e.g. an empty map and the output
the populated map.

Such a transformation is specified by combining multiple 
Transformers into a chain of Transformers. Once that's done
we can easily create the documentation on a line by line 
basis for each setter. Setters can be chained to create a 
function that manipulates some input.

For the time being, all intermediate terms are defined before
combining them into various expressions, i.e. not visible
in the documentation. This is a future extension.