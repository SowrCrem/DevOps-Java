# Use a base image with Maven and Java 11
FROM maven:3-jdk-11

# Install Pandoc and LaTeX
RUN apt-get update && \
    apt-get install -y pandoc texlive-latex-extra

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml /app
COPY src /app/src

# Build the application
RUN mvn package

# Copy the assembled application directory
COPY target /app/target

# Expose the port the application runs on
EXPOSE 5000

# Command to start the application using the shell script
CMD ["sh", "target/bin/simplewebapp"]
