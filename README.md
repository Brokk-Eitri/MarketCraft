# MarketCraft
## About
An economy-simulation plugin for MineCraft. <br>
Item prices are calculated based on their rarity, and can then be bought and sold at the GUI shop using in-game money.

## Details
Price is calculated by:
```java
rarity = 1 - ((count - lowest) / (highest - lowest))
price = min + (max - min) * rarity
```
`count`: The number of this item that have been collected. <br>
`lowest`, `highest`: The counts of the most and least common items. <br>
`min`, `max`, the minimum and maximum price an item can have. <br>

Count is affected by:
* Breaking blocks
* Placing blocks
* Crafting items
* Smelting items
* Mob drops
* Harvesting crops
