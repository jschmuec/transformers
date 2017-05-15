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
in the documentation. This is a potential future extension.

The following data structure.

    val chain =
        SetEntry("FirstName", Variable("GivenName")) +
        SetEntry("Name", Variable("LastName")) +
        SetEntry("Age", Add(Const(1), Const(42)))

Creates the following when run through the Documentor.

    <tr>
        <td>FirstName</td><td>=GivenName</td></tr>
    </tr>
    <tr>
        <td>Name</td><td>=LastName</td></tr>
    </tr>
    <tr>
        <td>Age</td><td>=1+42</td>
    </tr>

Wrapped into a table this looks like:

<table style="border:1px">
        <thead>
          <tr style="border:1px;">
            <th style="border:1px;">Target</th>
            <th style="border:1px;">Source</th>
          </tr>
        </thead>
        <tobdy>
          <tr>
            <tr><td>FirstName</td><td>=GivenName</td></tr>
          </tr><tr>
            <tr><td>Name</td><td>=LastName</td></tr>
          </tr><tr>
            <tr><td>Age</td><td>=1+42</td></tr>
          </tr>
        </tobdy>
      </table>