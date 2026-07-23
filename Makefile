.PHONY: feature

# Usage: make feature name=feature_name
feature:
	./gradlew generateFeature -PfeatureName=$(name)
