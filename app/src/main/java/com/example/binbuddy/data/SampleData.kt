package com.example.binbuddy.data

object SampleData {
    fun makeItems(): List<ItemEntity> = listOf(
        ItemEntity(title = "Milk",             location = "Aisle 1",    cost = "$2.99",  description = "1 gallon whole milk"),
        ItemEntity(title = "Bread",            location = "Bakery",     cost = "$1.99",  description = "Whole wheat loaf"),
        ItemEntity(title = "Eggs",             location = "Aisle 2",    cost = "$3.49",  description = "Dozen large eggs"),
        ItemEntity(title = "Cheese",           location = "Dairy",      cost = "$4.29",  description = "8 oz cheddar cheese"),
        ItemEntity(title = "Apples",           location = "Produce",    cost = "$0.99",  description = "Red delicious (per lb)"),
        ItemEntity(title = "Bananas",          location = "Produce",    cost = "$0.59",  description = "Yellow bananas (per lb)"),
        ItemEntity(title = "Chicken Breast",   location = "Meat",       cost = "$5.99",  description = "Boneless skinless (per lb)"),
        ItemEntity(title = "Rice",             location = "Aisle 3",    cost = "$2.49",  description = "2 lb bag of long grain rice"),
        ItemEntity(title = "Pasta",            location = "Aisle 3",    cost = "$1.29",  description = "16 oz spaghetti"),
        ItemEntity(title = "Orange Juice",     location = "Beverages",  cost = "$3.99",  description = "64 oz carton"),
        ItemEntity(title = "Coffee",           location = "Beverages",  cost = "$7.49",  description = "12 oz ground coffee"),
        ItemEntity(title = "Toilet Paper",     location = "Paper Goods",cost = "$6.99",  description = "12-roll pack"),
        ItemEntity(title = "Shampoo",          location = "Personal Care", cost = "$4.99", description = "12 oz bottle"),
        ItemEntity(title = "Soap",             location = "Personal Care", cost = "$1.49", description = "Bar soap"),
        ItemEntity(title = "Toothpaste",       location = "Personal Care", cost = "$2.99", description = "6 oz tube"),
        ItemEntity(title = "Butter",           location = "Dairy",      cost = "$3.49",  description = "1 lb salted butter"),
        ItemEntity(title = "Yogurt",           location = "Dairy",      cost = "$0.89",  description = "6 oz Greek yogurt"),
        ItemEntity(title = "Cereal",           location = "Aisle 2",    cost = "$3.99",  description = "18 oz granola cereal"),
        ItemEntity(title = "Lettuce",          location = "Produce",    cost = "$1.29",  description = "1 head of romaine"),
        ItemEntity(title = "Tomatoes",         location = "Produce",    cost = "$2.49",  description = "Vine-ripened (per lb)"),
        ItemEntity(title = "Onions",           location = "Produce",    cost = "$0.79",  description = "Yellow onions (per lb)"),
        ItemEntity(title = "Carrots",          location = "Produce",    cost = "$1.19",  description = "1 lb bag"),
        ItemEntity(title = "Sugar",            location = "Baking",     cost = "$2.29",  description = "4 lb bag"),
        ItemEntity(title = "Flour",            location = "Baking",     cost = "$1.99",  description = "5 lb all-purpose"),
        ItemEntity(title = "Canned Beans",     location = "Canned Goods", cost = "$0.89", description = "15 oz black beans")
    )
}
