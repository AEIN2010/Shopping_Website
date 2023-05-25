    public static List<ShoppingCartItem> StringToList(String input) {
        // Remove outer curly braces
        String cleanedInput = input;

        // Split by '},{'
        String[] sets = cleanedInput.split("\\},\\{");

        List<ShoppingCartItem> shoppingCartItems = new ArrayList<>();

        for (String set : sets) {
            // Split by comma
            String[] elements = set.split(",");

            // Create ShoppingCartItem object
            ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
            shoppingCartItem.prodId = elements[0];
            shoppingCartItem.quantity = (Integer.parseInt(elements[1]));
            // Assuming the date is in a specific format, you can parse it accordingly
            // Here's an example using the current date as a placeholder
            shoppingCartItem.updated = elements[2];
            shoppingCartItem.prodName = elements[3];

            shoppingCartItems.add(shoppingCartItem);
        }

        return shoppingCartItems;
    }


    public static String ListToString(List<ShoppingCartItem> shoppingCartItems) {
        List<String> sets = new ArrayList<>();

        for (ShoppingCartItem item : shoppingCartItems) {
            String set = item.prodId + "," + item.quantity + "," + item.updated + "," + item.prodName;
            sets.add(set);
        }

        String joinedSets = String.join("},{", sets);
        return  joinedSets;
    }



