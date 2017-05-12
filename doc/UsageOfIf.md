Usage of If
===

The `If` construct in our DSL is not delaying the creation of the expressions
which are passed in for the true and false value. So if you want to have
`SideEffects` at time of execution you have to use the SideEffect construct.

Here are examples:

```
    val i = If( false, 
                { println("evaluated true"); true},
                { println("evaluated false"); false
              )
``` 

will print

    > evaluated true
    > evaluated false

For delayed execution you need to use:

```
  val i = If( false,
            SideEffect( { println("eval true"); true } ),
            SideEffect( { println("eval flase"); false } )
            )
```

