#
# Dockerfile with Java-11
#
# https://github.com/dockerfile/ubuntu
# https://blobfolio.com/2018/12/using-docker-as-a-build-environment/

# Pull base image.
FROM ubuntu:18.04

LABEL CPPDocker.version="0.1-beta"
LABEL vendor="Devendra Rai, rai.devendra@yahoo.co.uk"
LABEL CPPDocker.release-date="2019-10-03"
LABEL CPPDocker.version.is-production="No"

ARG GTESTCOMMITID=cad3bc46c2ba8ec0bd7bb252d7279fe791140fbf
ARG USERNAME=dockeruser
ARG USER_UID=1000
ARG USER_GID=$USER_UID

# Tell debian that we are running non-interactively
ENV DEBIAN_FRONTEND=noninteractive

# Install Core components.
RUN \
  sed -i 's/# \(.*multiverse$\)/\1/g' /etc/apt/sources.list && \
  apt-get update && \
  apt-get -qqy upgrade && \
  apt-get -y install --no-install-recommends apt-utils dialog 2>&1 && \
  apt-get install -y build-essential && \
  apt-get install -y software-properties-common && \
  apt-get install -y sudo && \
  apt-get install -qqy curl && \
  apt-get install -qqy net-tools && \
  apt-get install -qqy wget && \
  apt-get install -qqy unzip && \
  add-apt-repository ppa:openjdk-r/ppa && \
  apt-get -qqy update && \
  apt-get install -qqy openjdk-11-jdk && \
  apt-get install -qqy locales && \
  apt-get purge -qqy --auto-remove build-essential && \
  apt-get purge -qqy --auto-remove software-properties-common && \
  #
  # Java tools recommended by Princeton University: https://lift.cs.princeton.edu/java/linux/
  #
  cd /usr/local && \
  curl -O "https://lift.cs.princeton.edu/java/linux/lift-cli.zip" && \
  unzip lift-cli.zip && \
  rm lift-cli.zip && \
  #
  # textbook libraries, DrJava, Findbugs, PMD, an Checkstyle.
  # https://algs4.cs.princeton.edu/linux/
  #
  mkdir -v /usr/local/algs4 && \
  chmod 755 /usr/local/algs4 && \
  cd /usr/local/algs4 && \
  curl -O "https://algs4.cs.princeton.edu/code/algs4.jar" && \
  curl -O "https://algs4.cs.princeton.edu/linux/{javac,java}-{algs4,cos226,coursera}" && \
  #echo "CWD: " `pwd` && \
  #echo "" && \
  #echo "Contents: "`ls -alh .` && \
  chmod 755 javac-algs4 javac-cos226 javac-coursera java-algs4 java-cos226 java-coursera && \
  #chmod 755 {javac,java}-{algs4,cos226,coursera} && \
  mv -t /usr/local/bin javac-algs4 javac-cos226 javac-coursera java-algs4 java-cos226 java-coursera  && \
  #echo "" && \
  #echo "Contents: "`ls /usr/local/bin` && \
  curl -O "https://algs4.cs.princeton.edu/linux/drjava.jar" && \
  curl -O "https://algs4.cs.princeton.edu/linux/drjava" && \
  chmod 755 drjava && \
  mv /usr/local/algs4/drjava /usr/local/bin && \
  curl -O "https://algs4.cs.princeton.edu/linux/.drjava" && \
  mv /usr/local/algs4/.drjava ~ && \
  curl -O "https://algs4.cs.princeton.edu/linux/findbugs.{zip,xml}" && \
  curl -O "https://algs4.cs.princeton.edu/linux/findbugs-{algs4,cos226,coursera}" && \
  unzip findbugs.zip && \
  rm findbugs.zip && \
  chmod 755 findbugs-algs4 findbugs-cos226 findbugs-coursera && \
  #chmod 755 /usr/local/algs4/findbugs-{algs4,cos226,coursera} && \
  mv -t /usr/local/bin findbugs-algs4 findbugs-cos226 findbugs-coursera && \
  curl -O "https://algs4.cs.princeton.edu/linux/pmd.{zip,xml}" && \
  curl -O "https://algs4.cs.princeton.edu/linux/pmd-{algs4,cos226,coursera}" && \
  #echo "PWD: "`pwd` && \
  #echo "Contents: "`ls -alh .` && \
  unzip /usr/local/algs4/pmd.zip && \
  rm /usr/local/algs4/pmd.zip && \
  chmod 755 pmd-algs4 pmd-cos226 pmd-coursera && \
  #chmod 755 /usr/local/algs4/pmd-{algs4,cos226,coursera} && \
  mv -t /usr/local/bin pmd-algs4 pmd-cos226 pmd-coursera && \
  curl -O "https://algs4.cs.princeton.edu/linux/checkstyle.zip"  && \
  curl -O "https://algs4.cs.princeton.edu/linux/checkstyle-suppressions.xml" && \
  curl -O "https://algs4.cs.princeton.edu/linux/checkstyle-{algs4,cos226,coursera}.xml" && \
  curl -O "https://algs4.cs.princeton.edu/linux/checkstyle-{algs4,cos226,coursera}" && \
  unzip /usr/local/algs4/checkstyle.zip && \
  rm /usr/local/algs4/checkstyle.zip && \
  chmod 755 checkstyle-algs4 checkstyle-cos226 checkstyle-coursera && \
  mv -t /usr/local/bin checkstyle-algs4 checkstyle-cos226 checkstyle-coursera && \
  #
  apt-get autoremove -y && \
  apt-get clean && \
  rm -rf /var/lib/apt/lists/* && \
  #
  # Setup locales
  sed -i -e 's/# en_US.UTF-8 UTF-8/en_US.UTF-8 UTF-8/' /etc/locale.gen && \
  dpkg-reconfigure --frontend=noninteractive locales && \
  update-locale LANG=en_US.UTF-8 && \
#
# Add non-admin user
  groupadd --gid $USER_GID $USERNAME && \
  useradd -s /bin/bash --uid $USER_UID --gid $USER_GID -m $USERNAME && \
  echo $USERNAME ALL=\(root\) NOPASSWD:ALL > /etc/sudoers.d/$USERNAME && \
  chmod 0440 /etc/sudoers.d/$USERNAME && \
#
# Add files for convenience.
#ADD root/.bashrc /home/dockeruser/.bashrc
#ADD root/.scripts /home/dockeruser/.scripts
#
#
# Make sure that all file in /home/dockeruser are owned by dockeruser
  chown -R dockeruser /home/dockeruser
  #ENV HOME /home/dockeruser && \
#

# Add default home for the non-admin user
WORKDIR /home/dockeruser
ADD --chown=dockeruser Configurations/.gitconfig /home/dockeruser/.gitconfig
ADD --chown=dockeruser Configurations/.bashrc /home/dockeruser/.bashrc
ADD --chown=dockeruser Configurations/git-completion.sh /home/dockeruser/git-completion.sh
ADD --chown=dockeruser Configurations/git-prompt.sh /home/dockeruser/git-prompt.sh

#ADD --chown=dockeruser VSCodeExtensions/extensions /home/dockeruser/.vscode-server/extensions

ENV LANG en_US.UTF-8
ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8

# Do the following as an ordinary user
USER dockeruser

RUN \
  chmod +x /home/dockeruser/.bashrc && \
  chmod +x /home/dockeruser/git-completion.sh && \
  chmod +x /home/dockeruser/git-prompt.sh


# Define default command.
CMD ["/bin/bash"]
