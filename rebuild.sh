./gradlew clean build installDist

mkdir -p build/local/rep0
mkdir -p build/local/rep1
mkdir -p build/local/rep2
mkdir -p build/local/rep3

cp -r build/install/library/* build/local/rep0/
cp -r build/install/library/* build/local/rep1/
cp -r build/install/library/* build/local/rep2/
cp -r build/install/library/* build/local/rep3/