with open('./output.txt','r',encoding='utf-16') as infile:
    with open('./output.csv','w') as outfile:
        for line in infile.readlines():
            if (not line.startswith('-')):
                outfile.write(line.split(' ')[1].replace('\n','')+',')
            else:
                outfile.write('\n')