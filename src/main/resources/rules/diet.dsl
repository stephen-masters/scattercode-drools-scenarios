[when][]I am given a meal of more than "{sizeLimit}" "{unit}" = $meal: Meal($size: {unit} > {sizeLimit}); $sizeLimit: Integer() from {sizeLimit}; $unit: String() from "{unit}"
[then][]Warn that the meal is too big = insertLogical(new MealTooBigWarning("That meal is bigger than your " + $sizeLimit + " " + $unit + " meal limit."));
