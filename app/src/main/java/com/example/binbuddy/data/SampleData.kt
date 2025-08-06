package com.example.binbuddy.data

import com.example.binbuddy.R

/**
 * Provides a reusable set of sample ItemEntity objects for seeding the database,
 * now including storeId assignments cycling among the first three stores.
 */
object SampleData {
    fun makeItems(): List<ItemEntity> = listOf(
        ItemEntity(title = "Milk",           location = "Aisle 1",    cost = "$2.99",  description = "1 gallon whole milk",     storeId = 1,  imageId = R.drawable.milk),
        ItemEntity(title = "Bread",          location = "Bakery",     cost = "$1.99",  description = "Whole wheat loaf",          storeId = 2, imageId = R.drawable.bread),
        ItemEntity(title = "Eggs",           location = "Aisle 2",    cost = "$3.49",  description = "Dozen large eggs",         storeId = 3, imageId = R.drawable.eggs),
        ItemEntity(title = "Cheese",         location = "Dairy",      cost = "$4.29",  description = "8 oz cheddar cheese",      storeId = 1, imageId = R.drawable.cheese)
    )
}

/*
ItemEntity(title = "Apples",         location = "Produce",    cost = "$0.99",  description = "Red delicious (per lb)",    storeId = 2),
ItemEntity(title = "Bananas",        location = "Produce",    cost = "$0.59",  description = "Yellow bananas (per lb)",  storeId = 3),
ItemEntity(title = "Chicken Breast", location = "Meat",       cost = "$5.99",  description = "Boneless skinless (per lb)", storeId = 1),
ItemEntity(title = "Rice",           location = "Aisle 3",    cost = "$2.49",  description = "2 lb bag of long grain rice",storeId = 2),
ItemEntity(title = "Pasta",          location = "Aisle 3",    cost = "$1.29",  description = "16 oz spaghetti",          storeId = 3),
ItemEntity(title = "Orange Juice",   location = "Beverages",  cost = "$3.99",  description = "64 oz carton",             storeId = 1),
ItemEntity(title = "Coffee",         location = "Beverages",  cost = "$7.49",  description = "12 oz ground coffee",      storeId = 2),
ItemEntity(title = "Toilet Paper",   location = "Paper Goods",cost = "$6.99",  description = "12-roll pack",             storeId = 3),
ItemEntity(title = "Shampoo",        location = "Personal Care", cost = "$4.99",description = "12 oz bottle",               storeId = 1),
ItemEntity(title = "Soap",           location = "Personal Care", cost = "$1.49",description = "Bar soap",                   storeId = 2),
ItemEntity(title = "Toothpaste",     location = "Personal Care", cost = "$2.99",description = "6 oz tube",                  storeId = 3),
ItemEntity(title = "Butter",         location = "Dairy",      cost = "$3.49",  description = "1 lb salted butter",         storeId = 1),
ItemEntity(title = "Yogurt",         location = "Dairy",      cost = "$0.89",  description = "6 oz Greek yogurt",         storeId = 2),
ItemEntity(title = "Cereal",         location = "Aisle 2",    cost = "$3.99",  description = "18 oz granola cereal",       storeId = 3),
ItemEntity(title = "Lettuce",        location = "Produce",    cost = "$1.29",  description = "1 head of romaine",          storeId = 1),
ItemEntity(title = "Tomatoes",       location = "Produce",    cost = "$2.49",  description = "Vine-ripened (per lb)",      storeId = 2),
ItemEntity(title = "Onions",         location = "Produce",    cost = "$0.79",  description = "Yellow onions (per lb)",     storeId = 3),
ItemEntity(title = "Carrots",        location = "Produce",    cost = "$1.19",  description = "1 lb bag",                   storeId = 1),
ItemEntity(title = "Sugar",          location = "Baking",     cost = "$2.29",  description = "4 lb bag",                   storeId = 2),
ItemEntity(title = "Flour",          location = "Baking",     cost = "$1.99",  description = "5 lb all-purpose",           storeId = 3),
ItemEntity(title = "Canned Beans",   location = "Canned Goods", cost = "$0.89", description = "15 oz black beans",          storeId = 1) */
