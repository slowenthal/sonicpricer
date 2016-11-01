### Sonic Pricer

A class to find the lowest price for a collection of items given individual prices for each item as well special prices for bundles of items.

There is a single external method called price.  It simply takes a Map of SKU -> quantity representing the basket of items to price, and returns the lowest price.  This method assumes that 
there exists an individual price for every item, and will throw if one does not exist.  The method is threadsafe as it works exclusively with immutable data.  The local variables exist for readability.

## Some code objectives

- The parameter times attempt to use the minimal interface.  eg. Take a PartialFunction instead of Map if only apply() and isDefinedAt() are required
- Use descriptive Type names suce as `PriceType` instead of `BigDecimal` to make type paremeters readable and allow for easy refactoring

## Future Enhancements

- Optimizations for certain types of bundles we can consume repeats and other heuristics without working out all combinations.  This is a huge gain in cases where the basket contains a large number of repeated bundles, and the bundles are disjoint.
- Allow bundles that specify categories of items as well as specific SKUs - eg Buy a cell phone and any 2 accesories ...
- Limit the quantity of a bundle used - so emit the bundle combination as well as the 
- Allow bad baskets, and return a Try[PriceType]


