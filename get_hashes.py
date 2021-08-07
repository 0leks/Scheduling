
import hashlib

BLOCKSIZE = 65536

def getHash(filepath):
    hasher = hashlib.sha512()
    with open(filepath, 'rb') as afile:
        buf = afile.read(BLOCKSIZE)
        while len(buf) > 0:
            hasher.update(buf)
            buf = afile.read(BLOCKSIZE)
    return hasher.hexdigest()



filelist = ['scheduling.exe', 'schedulinglauncher.exe']

hashes = {filepath: getHash(filepath) for filepath in filelist}


print(hashes)

with open("latesthashes.textfile","w+") as outputfile:
    for filepath in hashes:
        outputfile.write(f'{filepath}:{hashes[filepath]}\n')

