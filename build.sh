ENGINE_PATH=~/projects/abalone-engine/abalone_engine/lib
cd abalone
mvn assembly:assembly
cp target/abalone-latest-jar-with-dependencies.jar ${ENGINE_PATH}